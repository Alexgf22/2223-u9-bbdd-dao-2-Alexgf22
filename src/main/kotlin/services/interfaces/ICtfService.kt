package services.interfaces

import dao.entity.CTF

/**
 * Interfaz que implementará la clase GrupoCtfServiceImplementación los metodos de añadir, obtener
 * participación, actualizar , eliminar y obtener todos los Ctfs.
 */
interface ICtfService {

    fun anadirCtf(ctf: CTF)
    fun obtenerParticipacionCtf(id: Int, grupoid: Int): CTF?
    fun actualizarCtf(ctf: CTF)
    fun eliminarCtf(id: Int, grupoid: Int)
    fun obtenerTodosCtfs(): List<CTF>

}