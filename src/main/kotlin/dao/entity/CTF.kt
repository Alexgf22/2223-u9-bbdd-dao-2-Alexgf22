package dao.entity

/**
 * @property ctfId : Int   Identificador del CTF.
 * @property grupoid : Int   Identificador del grupo que participa en el CTF.
 * @property puntuacion : Int   Puntuación lograda.
 */
data class CTF(
    val ctfId: Int,
    val grupoid: Int,
    val puntuacion: Int
)
