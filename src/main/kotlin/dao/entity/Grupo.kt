package dao.entity

/**
 * @property grupoid : Int   Identificador del grupo.
 * @property grupodesc:String  Descripción del grupo.
 * @property mejorPosCTFid: Int  Id del CTF en el que ha logrado la mejor posición.
 */
data class Grupo(
    var grupoid: Int,
    val grupodesc: String,
    var mejorPosCTFid: Int? = null
)