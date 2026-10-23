package com.rifaapp.core.domain.model

/**
 * Estado de un número del tablero.
 *
 * Importante: el estado NO se guarda como dato editable, se DERIVA del dinero pagado.
 * Ver [desde].
 */
enum class EstadoNumero {
    DISPONIBLE,
    RESERVADO,
    ABONADO,
    PAGADO,
    ;

    companion object {

        /**
         * Deriva el estado a partir del monto pagado y el precio de la boleta.
         *
         * - Sin pagos -> RESERVADO
         * - Pagó parte -> ABONADO
         * - Pagó el total o más -> PAGADO
         */
        fun desde(montoPagado: Long, precioBoleta: Long): EstadoNumero = when {
            montoPagado <= 0L -> RESERVADO
            montoPagado < precioBoleta -> ABONADO
            else -> PAGADO
        }
    }
}
