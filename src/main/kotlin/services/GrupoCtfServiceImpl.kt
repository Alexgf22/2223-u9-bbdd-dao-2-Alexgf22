package services

import dao.GrupoCtfDAO
import dao.entity.CTF
import dao.entity.Grupo
import services.interfaces.ICtfService
import services.interfaces.IGrupoService

class GrupoCtfServiceImpl(private val grupoCtfDao: GrupoCtfDAO): ICtfService, IGrupoService {
    override fun anadirCtf(ctf: CTF) {
        return grupoCtfDao.anadirCtf(ctf)
    }

    override fun obtenerParticipacionCtf(id: Int, grupoid: Int): CTF? {
        return grupoCtfDao.obtenerParticipacionCtf(id, grupoid)
    }

    override fun actualizarCtf(ctf: CTF) {
        return grupoCtfDao.actualizarCtf(ctf)
    }

    override fun eliminarCtf(id: Int, grupoid: Int) {
        return grupoCtfDao.eliminarCtf(id, grupoid)
    }

    override fun obtenerTodosCtfs(): List<CTF> {
        return grupoCtfDao.obtenerTodosCtfs()
    }


    override fun crearGrupo(grupo: Grupo) {
        return grupoCtfDao.crearGrupo(grupo)
    }

    override fun obtenerGrupo(id: Int): Grupo? {
        return grupoCtfDao.obtenerGrupo(id)
    }

    override fun actualizarMejorPosCtf(grupo: Grupo) {
        return grupoCtfDao.actualizarMejorPosCtf(grupo)
    }

    override fun eliminarGrupo(id: Int) {
        return grupoCtfDao.eliminarGrupo(id)
    }

    override fun obtenerTodosGrupos(): List<Grupo> {
        return grupoCtfDao.obtenerTodosGrupos()
    }

    override fun eliminarTodosGrupos() {
        return grupoCtfDao.eliminarTodosGrupos()
    }
}