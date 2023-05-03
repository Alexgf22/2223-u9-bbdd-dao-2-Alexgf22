package services.interfaces

import dao.entity.CTF

interface ICtfService {

    fun crearCtf(ctf: CTF)
    fun obtenerCtf(id: Int): CTF?
    fun actualizarCtf(ctf: CTF)
    fun eliminarCtf(id: Int)
    fun obtenerTodosCtfs(): List<CTF>

}