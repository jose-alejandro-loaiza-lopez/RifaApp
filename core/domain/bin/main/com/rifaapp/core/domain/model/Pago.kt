package com.rifaapp.core.domain.model

import java.time.Instant

/** Un abono individual sobre una boleta. */
data class Pago(
    val id: Long = 0L,
    val boletaId: Long,
    /** Monto en centavos. Siempre > 0. */
    val monto: Long,
    val fecha: Instant,
    val nota: String? = null,
)
