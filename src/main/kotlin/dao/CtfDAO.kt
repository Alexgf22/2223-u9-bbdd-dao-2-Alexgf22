package dao

import dao.entity.CTF
import dao.interfaces.ICtfDao
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource


class CtfDAO(private val dataSource: DataSource): ICtfDao {

    private val conexion: Connection = DriverManager.getConnection("jdbc:h2:mem:test")

    init {
        conexion.createStatement().executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS CTFS (
                CTFid INT NOT NULL,
                grupoid INT NOT NULL,
                puntuacion INT NOT NULL,
                PRIMARY KEY (CTFid,grupoid)
            )
        """.trimIndent()
        )
    }


    /**
     * Realizamos una sentencia, donde insertamos en la tabla CTFS una nueva fila con los datos
     * correspondientes del ctf que le pasamos a la función por parámetro, por tanto en cada índice
     * de la fila en orden ascendiente añadimos cada uno de los parámetros de dicho ctf. Después
     * ejecutamos dicha sentencia para que la tabla se actualice con los nuevos datos.
     */
    override fun crearCtf(ctf: CTF) {
        val sql = "INSERT INTO CTFS(CTFid, grupoid, puntuacion) VALUES(?, ?, ?)"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, ctf.CTFid)
                stmt.setInt(2, ctf.grupoid)
                stmt.setInt(3, ctf.puntuacion)
                ctf
            }
        }
    }

    override fun obtenerCtf(id: Int): CTF? {
        val sql2 = "SELECT * FROM CTFS WHERE CTFid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql2).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    CTF(
                        rs.getInt("CTFid"),
                        rs.getInt("grupoid"),
                        rs.getInt("puntuacion")
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun actualizarCtf(ctf: CTF) {
        val sql3 = "UPDATE CTFS SET grupoid=?, puntuacion=? WHERE CTFid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql3).use { stmt ->
                stmt.setInt(1, ctf.grupoid)
                stmt.setInt(2, ctf.puntuacion)
                stmt.setInt(3, ctf.CTFid)
                stmt.executeUpdate()
                ctf
            }
        }
    }


    /**
     *
     */
    override fun eliminarCtf(id: Int) {
        val sql4 = "DELETE FROM CTFS WHERE CTFid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql4).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
        }
    }


    override fun obtenerTodosCtfs(): MutableList<CTF> {
        val sql5 = "SELECT * FROM CTFS"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql5).use { stmt ->
                val rs = stmt.executeQuery()
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
                ctfs
            }
        }
    }




}