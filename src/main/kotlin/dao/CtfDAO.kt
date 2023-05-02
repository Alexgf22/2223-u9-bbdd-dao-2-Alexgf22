package dao

import dao.entity.CTF
import dao.interfaces.ICtfDao
import java.sql.Connection
import java.sql.DriverManager


class CtfDAO: ICtfDao {

    private val conexion: Connection = DriverManager.getConnection("jdbc:h2:mem:test")

    init {
        conexion.createStatement().executeUpdate("""
            CREATE TABLE IF NOT EXISTS CTFS (
                CTFid INT NOT NULL,
                grupoid INT NOT NULL,
                puntuacion INT NOT NULL,
                PRIMARY KEY (CTFid,grupoid)
            )
        """.trimIndent())
    }


    /**
     * Realizamos una sentencia, donde insertamos en la tabla CTFS una nueva fila con los datos
     * correspondientes del ctf que le pasamos a la función por parámetro, por tanto en cada índice
     * de la fila en orden ascendiente añadimos cada uno de los parámetros de dicho ctf. Después
     * ejecutamos dicha sentencia para que la tabla se actualice con los nuevos datos.
     */
    override fun crearCtf(ctf: CTF) {
        val stmt = conexion.prepareStatement("""
            INSERT INTO CTFS(CTFid, grupoid, puntuacion) VALUES(?, ?, ?)
        """.trimIndent())
        stmt.setInt(1, ctf.CTFid)
        stmt.setInt(2, ctf.grupoid)
        stmt.setInt(3, ctf.puntuacion)
        stmt.executeUpdate()
    }

    override fun obtenerCtf(id: Int): CTF? {
        val stmt = conexion.prepareStatement("""
            SELECT * FROM CTFS WHERE CTFid=?
        """.trimIndent())
        stmt.setInt(1, id)
        val rs = stmt.executeQuery()
        return if (rs.next()) {
            CTF(
                rs.getInt("CTFid"),
                rs.getInt("grupoid"),
                rs.getInt("puntuacion")
            )
        } else {
            null
        }
    }

    override fun actualizarCtf(ctf: CTF) {
        val stmt = conexion.prepareStatement("""
            UPDATE CTFS SET grupoid=?, puntuacion=? WHERE CTFid=?
        """.trimIndent())
        stmt.setInt(1, ctf.grupoid)
        stmt.setInt(2, ctf.puntuacion)
        stmt.setInt(3, ctf.CTFid)
        stmt.executeUpdate()
    }

    /**
     *
     */
    override fun eliminarCtf(id: Int) {
        val stmt = conexion.prepareStatement("""
            DELETE FROM CTFS WHERE CTFid=?
        """.trimIndent())
        stmt.setInt(1, id)
        stmt.executeUpdate()
    }

    override fun obtenerTodosCtfs(): MutableList<CTF> {
        val stmt = conexion.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM CTFS")
        val ctfs = mutableListOf<CTF>()
        while (rs.next()) {
            ctfs.add(
                CTF(
                    rs.getInt("CTFid"),
                    rs.getInt("grupoid"),
                    rs.getInt("puntuacion")
                )
            )
        }
        return ctfs
    }


}