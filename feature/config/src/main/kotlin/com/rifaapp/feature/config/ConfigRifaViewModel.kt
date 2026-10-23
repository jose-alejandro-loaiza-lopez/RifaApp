package com.rifaapp.feature.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifaapp.core.designsystem.textoVisible
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.BoletaRepository
import com.rifaapp.core.domain.usecase.GuardarConfiguracionRifa
import com.rifaapp.core.domain.usecase.ObservarTablero
import com.rifaapp.core.domain.validation.ValidadorRifa
import com.rifaapp.core.domain.validation.ValidadorTelefono
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigRifaViewModel(
    observarTablero: ObservarTablero,
    private val boletas: BoletaRepository,
    private val guardarConfiguracion: GuardarConfiguracionRifa,
) : ViewModel() {

    private val guardado = MutableStateFlow(false)
    private val guardando = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    val state: StateFlow<ConfigRifaUiState> =
        combine(observarTablero(), guardado, guardando, mensaje) { tablero, ok, g, texto ->
            ConfigRifaUiState(
                cargando = false,
                rifa = tablero?.rifa ?: rifaPorDefecto(1L),
                numeros = tablero?.numeros.orEmpty(),
                boletas = tablero?.boletas.orEmpty(),
                guardado = ok,
                guardando = g,
                mensaje = texto,
            )
        }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ConfigRifaUiState(),
            )

    fun guardar(formulario: FormularioRifa) {
        val rifaActual = state.value.rifa ?: return

        val precio = formulario.precioCentavos
        val inicio = formulario.numeroInicioTexto.toIntOrNull()
        val fin = formulario.numeroFinTexto.toIntOrNull()
        if (precio == null) {
            mensaje.value = "El precio debe ser un número válido."
            return
        }
        if (inicio == null || fin == null) {
            mensaje.value = "El rango debe ser numérico."
            return
        }
        if (fin < inicio) {
            mensaje.value = "El fin debe ser mayor o igual que el inicio."
            return
        }

        val digitos = maxOf(
            inicio.toString().length,
            fin.toString().length,
        ).coerceAtLeast(1)

        val columnas = formulario.columnasTexto.toIntOrNull()?.coerceIn(1, 12) ?: rifaActual.columnas

        val nueva = rifaActual.copy(
            titulo = formulario.titulo.trim(),
            descripcion = formulario.descripcion.trim(),
            loteria = formulario.loteria.trim().ifBlank { null },
            precioBoleta = precio,
            fechaSorteo = formulario.fechaSorteo,
            numeroInicio = inicio,
            numeroFin = fin,
            cantidadDigitos = digitos,
            columnas = columnas,
        )

        viewModelScope.launch {
            guardando.value = true
            when (val resultado = guardarConfiguracion(nueva)) {
                is Resultado.Exito -> {
                    guardado.value = true
                }

                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
            guardando.value = false
        }
    }

    fun restaurarPorDefecto() {
        val rifaActual = state.value.rifa ?: return

        viewModelScope.launch {
            guardando.value = true
            runCatching {
                boletas.obtenerPorRifa(rifaActual.id).forEach { boletas.liberar(it.id) }
            }
            val defaultRifa = Rifa(
                id = rifaActual.id,
                titulo = "Rifa del combo familiar",
                descripcion = "Rifa de ejemplo",
                fechaSorteo = LocalDate.of(2026, 12, 24),
                precioBoleta = 1_000_000L,
                numeroInicio = 0,
                numeroFin = 99,
                cantidadDigitos = 2,
                columnas = 5,
            )
            when (val resultado = guardarConfiguracion(defaultRifa)) {
                is Resultado.Exito -> {
                    guardado.value = true
                    mensaje.value = "Rifa restaurada por defecto"
                }

                is Resultado.Fallo -> mensaje.value = resultado.error.textoVisible()
            }
            guardando.value = false
        }
    }

    suspend fun generarRespaldoJson(): String? {
        val rifa = state.value.rifa ?: return null
        val boletaList = boletas.obtenerPorRifa(rifa.id)
        val root = JSONObject()
        root.put("version", 1)
        val r = JSONObject()
        r.put("id", rifa.id)
        r.put("titulo", rifa.titulo)
        r.put("descripcion", rifa.descripcion)
        r.put("loteria", rifa.loteria ?: JSONObject.NULL)
        r.put("fechaSorteo", rifa.fechaSorteo?.toString())
        r.put("precioBoleta", rifa.precioBoleta)
        r.put("numeroInicio", rifa.numeroInicio)
        r.put("numeroFin", rifa.numeroFin)
        r.put("cantidadDigitos", rifa.cantidadDigitos)
        r.put("columnas", rifa.columnas)
        root.put("rifa", r)
        val arr = JSONArray()
        for (b in boletaList) {
            val o = JSONObject()
            o.put("numero", b.numero)
            o.put("clienteNombre", b.cliente.nombre)
            o.put("clienteTelefono", b.cliente.telefonoE164)
            o.put("notas", b.notas ?: JSONObject.NULL)
            val pagos = JSONArray()
            for (p in b.pagos) {
                val po = JSONObject()
                po.put("monto", p.monto)
                po.put("fecha", p.fecha.toEpochMilli())
                po.put("nota", p.nota ?: JSONObject.NULL)
                pagos.put(po)
            }
            o.put("pagos", pagos)
            arr.put(o)
        }
        root.put("boletas", arr)
        return root.toString(2)
    }

    suspend fun importarRespaldoJson(contenido: String) {
        try {
            val root = JSONObject(contenido)
            val r = root.getJSONObject("rifa")
            val rifaActual = state.value.rifa ?: return
            val fechaSorteo = if (r.isNull("fechaSorteo")) null else {
                val texto = r.optString("fechaSorteo", "")
                if (texto.isBlank() || texto == "null") null else LocalDate.parse(texto)
            }
            val rifa = Rifa(
                id = rifaActual.id,
                titulo = r.optString("titulo", rifaActual.titulo),
                descripcion = r.optString("descripcion", rifaActual.descripcion),
                loteria = if (r.isNull("loteria")) null else r.optString("loteria").ifBlank { null },
                fechaSorteo = fechaSorteo ?: rifaActual.fechaSorteo,
                precioBoleta = r.optLong("precioBoleta", rifaActual.precioBoleta),
                numeroInicio = r.optInt("numeroInicio", rifaActual.numeroInicio),
                numeroFin = r.optInt("numeroFin", rifaActual.numeroFin),
                cantidadDigitos = r.optInt("cantidadDigitos", rifaActual.cantidadDigitos),
                columnas = r.optInt("columnas", rifaActual.columnas),
            )

            // Validar configuración antes de tocar datos.
            val validacionRifa = ValidadorRifa.validar(rifa)
            if (validacionRifa is Resultado.Fallo) {
                mensaje.value = validacionRifa.error.textoVisible()
                return
            }

            // Parsear y validar todas las boletas antes de borrar nada.
            data class BoletaImportada(
                val numero: Int,
                val cliente: Cliente,
                val notas: String?,
                val pagos: List<Triple<Long, Long, String?>>,
            )

            val arr = root.optJSONArray("boletas") ?: JSONArray()
            val importadas = mutableListOf<BoletaImportada>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val numero = o.getInt("numero")
                if (!rifa.contiene(numero)) {
                    mensaje.value = "El número $numero está fuera del rango de la rifa."
                    return
                }
                val nombre = o.optString("clienteNombre").trim()
                if (nombre.isBlank()) {
                    mensaje.value = "Hay una boleta sin nombre de cliente."
                    return
                }
                val telefonoNormalizado = ValidadorTelefono.normalizar(o.optString("clienteTelefono"))
                    ?: run {
                        mensaje.value = "Teléfono inválido en el respaldo: ${o.optString("clienteTelefono")}"
                        return
                    }
                val notas = if (o.isNull("notas")) null else o.optString("notas")
                val pagosArray = o.optJSONArray("pagos") ?: JSONArray()
                val pagos = mutableListOf<Triple<Long, Long, String?>>() // monto, fechaMillis, nota
                for (j in 0 until pagosArray.length()) {
                    val p = pagosArray.getJSONObject(j)
                    val monto = p.optLong("monto")
                    if (monto <= 0L) {
                        mensaje.value = "Hay un pago con monto inválido en el respaldo."
                        return
                    }
                    pagos.add(
                        Triple(
                            monto,
                            p.optLong("fecha"),
                            if (p.isNull("nota")) null else p.optString("nota"),
                        )
                    )
                }
                importadas.add(
                    BoletaImportada(
                        numero = numero,
                        cliente = Cliente(nombre, telefonoNormalizado),
                        notas = notas,
                        pagos = pagos,
                    )
                )
            }

            // Aplicar: primero borrar las boletas actuales, luego guardar la rifa y las nuevas boletas.
            for (b in boletas.obtenerPorRifa(rifa.id)) {
                boletas.liberar(b.id)
            }
            val resultadoRifa = guardarConfiguracion(rifa)
            if (resultadoRifa is Resultado.Fallo) {
                mensaje.value = resultadoRifa.error.textoVisible()
                return
            }

            for (imp in importadas) {
                val result = boletas.guardar(
                    Boleta(
                        rifaId = rifa.id,
                        numero = imp.numero,
                        cliente = imp.cliente,
                        notas = imp.notas,
                    )
                )
                val boletaId = when (result) {
                    is Resultado.Exito<*> -> (result.valor as Boleta).id
                    else -> null
                } ?: continue
                for ((monto, fechaMillis, nota) in imp.pagos) {
                    boletas.registrarPago(
                        boletaId = boletaId,
                        monto = monto,
                        fecha = Instant.ofEpochMilli(fechaMillis),
                        nota = nota,
                    )
                }
            }

            mensaje.value = "Respaldo importado correctamente."
        } catch (e: Exception) {
            mensaje.value = "No se pudo importar el respaldo."
        }
    }

    fun consumirMensaje() {
        mensaje.value = null
    }

    fun consumirGuardado() {
        guardado.value = false
    }

    private fun rifaPorDefecto(id: Long): Rifa = Rifa(
        id = id,
        titulo = "Rifa del combo familiar",
        descripcion = "Rifa de ejemplo",
        fechaSorteo = LocalDate.of(2026, 12, 24),
        precioBoleta = 1_000_000L,
        numeroInicio = 0,
        numeroFin = 99,
        cantidadDigitos = 2,
        columnas = 5,
    )
}