package dao.interfaces

import dao.entity.Grupo

/**
 * Interfaz que implementará la clase GrupoCtfDAO los metodos de crear, obtener
 * grupo, actualizarMejorPosCtf , eliminar, obtener y eliminar todos los Grupos.
 */
interface IGrupoDao {
    fun crearGrupo(grupo: Grupo)
    fun obtenerGrupo(id: Int): Grupo?
    fun actualizarMejorPosCtf(grupo: Grupo)
    fun eliminarGrupo(id: Int)
    fun obtenerTodosGrupos(): List<Grupo>
    fun eliminarTodosGrupos()
}


