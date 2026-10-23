package com.rifaapp.core.domain.model

/** Vista completa del tablero de una rifa, lista para pintar en la UI. */
data class Tablero(
    val rifa: Rifa,
    val numeros: List<Numero>,
    val recaudado: Long,
    /** Boletas activas, para mostrar el detalle de un número sin otra consulta. */
    val boletas: List<Boleta>,
) {
    val resumen: Resumen
        get() = Resumen(
            disponibles = numeros.count { it.estado == EstadoNumero.DISPONIBLE },
            reservados = numeros.count { it.estado == EstadoNumero.RESERVADO },
            abonados = numeros.count { it.estado == EstadoNumero.ABONADO },
            pagados = numeros.count { it.estado == EstadoNumero.PAGADO },
            recaudado = recaudado,
            potencial = rifa.precioBoleta * rifa.cantidadNumeros,
        )
}

data class Resumen(
    val disponibles: Int,
    val reservados: Int,
    val abonados: Int,
    val pagados: Int,
    val recaudado: Long,
    val potencial: Long,
) {
    val vendidos: Int get() = reservados + abonados + pagados
}
