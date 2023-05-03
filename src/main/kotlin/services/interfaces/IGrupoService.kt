package services.interfaces

import dao.entity.Grupo

interface IGrupoService {

    fun crearGrupo(grupo: Grupo)
    fun obtenerGrupo(id: Int): Grupo?
    fun actualizarMejorPosCtf(grupo: Grupo)
    fun eliminarGrupo(id: Int)
    fun obtenerTodosGrupos(): List<Grupo>
    fun eliminarTodosGrupos()

}