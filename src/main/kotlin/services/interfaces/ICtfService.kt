package services.interfaces

import dao.entity.CTF

interface ICtfService {

    fun anadirCtf(ctf: CTF)
    fun obtenerParticipacionCtf(id: Int, grupoid: Int): CTF?
    fun actualizarCtf(ctf: CTF)
    fun eliminarCtf(id: Int, grupoid: Int)
    fun obtenerTodosCtfs(): List<CTF>

}