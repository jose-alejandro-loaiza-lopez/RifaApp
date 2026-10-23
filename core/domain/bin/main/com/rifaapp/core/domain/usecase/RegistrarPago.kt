package com.rifaapp.core.domain.usecase

import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.repository.BoletaRepository
import java.time.Instant

/**
 * Registra un abono. El estado de la boleta (ABONADO / PAGADO) se recalcula solo
 * a partir de la suma de pagos; este caso de uso no decide estados.
 */
class RegistrarPago(
    private val boletas: BoletaRepository,
) {
    suspend operator fun invoke(
        boletaId: Long,
        monto: Long,
        fecha: Instant = Instant.now(),
        nota: String? = null,
    ): Resultado<Boleta> {
        if (monto <= 0L) return Resultado.Fallo(ErrorDominio.MontoInvalido)
        return boletas.registrarPago(boletaId = boletaId, monto = monto, fecha = fecha, nota = nota)
    }
}
