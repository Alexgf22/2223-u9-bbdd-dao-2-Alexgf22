package services

import dao.CtfDAO
import dao.entity.CTF
import services.interfaces.ICtfService

class CtfServiceImpl(private val ctfDao: CtfDAO): ICtfService {
    override fun crearCtf(ctf: CTF) {
        return ctfDao.crearCtf(ctf)
    }

    override fun obtenerCtf(id: Int): CTF? {
        return ctfDao.obtenerCtf(id)
    }

    override fun actualizarCtf(ctf: CTF) {
        return ctfDao.actualizarCtf(ctf)
    }

    override fun eliminarCtf(id: Int) {
        return ctfDao.eliminarCtf(id)
    }

    override fun obtenerTodosCtfs(): List<CTF> {
        return ctfDao.obtenerTodosCtfs()
    }
}