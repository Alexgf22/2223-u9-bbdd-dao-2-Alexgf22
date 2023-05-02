package dao.interfaces

import dao.entity.Grupo

interface IGrupoDao {
    fun crearGrupo(grupo: Grupo)
    fun obtenerGrupo(grupoId: Int): Grupo?
    fun actualizarGrupo(grupo: Grupo)
    fun eliminarGrupo(grupoId: Int)
    fun obtenerTodosGrupos(): List<Grupo>
    fun eliminarTodosGrupos()
}


