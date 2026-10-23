package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.BoletaRepository
import com.rifaapp.core.domain.validation.ValidadorTelefono

/** Reserva un número para un cliente, sin registrar dinero todavía. */
class ReservarNumero(
    private val boletas: BoletaRepository,
) {
    suspend operator fun invoke(
        rifa: Rifa,
        numero: Int,
        nombre: String,
        telefono: String,
    ): Resultado<Boleta> {
        if (!rifa.contiene(numero)) return Resultado.Fallo(ErrorDominio.NumeroFueraDeRango)
        if (nombre.isBlank()) return Resultado.Fallo(ErrorDominio.NombreVacio)

        val telefonoNormalizado = ValidadorTelefono.normalizar(telefono)
            ?: return Resultado.Fallo(ErrorDominio.TelefonoInvalido)

        val existente = boletas.obtenerPorNumero(rifa.id, numero)
        if (existente != null) return Resultado.Fallo(ErrorDominio.NumeroNoDisponible)

        return boletas.guardar(
            Boleta(
                rifaId = rifa.id,
                numero = numero,
                cliente = Cliente(nombre = nombre.trim(), telefonoE164 = telefonoNormalizado),
            ),
        )
    }
}
