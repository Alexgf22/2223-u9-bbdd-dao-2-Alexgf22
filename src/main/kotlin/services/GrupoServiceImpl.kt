package services

import dao.GrupoDAO
import dao.entity.Grupo
import services.interfaces.IGrupoService

class GrupoServiceImpl(private val grupoDao: GrupoDAO): IGrupoService {
    override fun crearGrupo(grupo: Grupo) {
        return grupoDao.crearGrupo(grupo)
    }

    override fun obtenerGrupo(id: Int): Grupo? {
        return grupoDao.obtenerGrupo(id)
    }

    override fun actualizarMejorPosCtf(grupo: Grupo) {
        return grupoDao.actualizarMejorPosCtf(grupo)
    }

    override fun eliminarGrupo(id: Int) {
        return grupoDao.eliminarGrupo(id)
    }

    override fun obtenerTodosGrupos(): List<Grupo> {
        return grupoDao.obtenerTodosGrupos()
    }

    override fun eliminarTodosGrupos() {
        return grupoDao.eliminarTodosGrupos()
    }
}