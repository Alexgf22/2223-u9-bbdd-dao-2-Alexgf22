package dao.interfaces

import dao.entity.CTF

interface ICtfDao {
    fun crearCtf(ctf: CTF)
    fun obtenerCtf(id: Int): CTF?
    fun actualizarCtf(ctf: CTF)
    fun eliminarCtf(id: Int)
}


