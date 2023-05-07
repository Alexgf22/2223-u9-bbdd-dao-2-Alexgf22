package dao

import dao.entity.CTF
import dao.entity.Grupo
import dao.interfaces.ICtfDao
import dao.interfaces.IGrupoDao
import java.sql.Connection
import javax.sql.DataSource

class GrupoCtfDAO(private val dataSource: DataSource): IGrupoDao, ICtfDao {

    private fun obtenerConexion(): Connection {
        return dataSource.connection
    }

    init {

        obtenerConexion().createStatement().executeUpdate("""
            CREATE TABLE IF NOT EXISTS CTFS (
                CTFid INT NOT NULL,
                grupoid INT NOT NULL,
                puntuacion INT NOT NULL,
                PRIMARY KEY (CTFid,grupoid)
            )
        """.trimIndent()
        )


        obtenerConexion().createStatement().executeUpdate("""
            CREATE TABLE IF NOT EXISTS GRUPOS (
            grupoid INT NOT NULL AUTO_INCREMENT,
            grupodesc VARCHAR(100) NOT NULL,
            mejorposCTFid INT,
            PRIMARY KEY (grupoid)
);
        """.trimIndent())

        obtenerConexion().createStatement().executeUpdate("ALTER TABLE GRUPOS ADD FOREIGN KEY (mejorposCTFid, grupoid) REFERENCES CTFS(CTFid,grupoid)")


        val resultSet = obtenerConexion().createStatement().executeQuery("SELECT COUNT(*) FROM grupos")
        resultSet.next()

        // Se comprueba si el número de filas es 0 para que no haya conflictos con los datos
        if (resultSet.getInt(1) == 0) {
            obtenerConexion().createStatement().executeUpdate("insert into grupos(grupoid, grupodesc) values(1, '1DAM-G1')")
            obtenerConexion().createStatement().executeUpdate("insert into grupos(grupoid, grupodesc) values(2, '1DAM-G2')")
            obtenerConexion().createStatement().executeUpdate("insert into grupos(grupoid, grupodesc) values(3, '1DAM-G3')")
            obtenerConexion().createStatement().executeUpdate("insert into grupos(grupoid, grupodesc) values(4, '1DAW-G1')")
            obtenerConexion().createStatement().executeUpdate("insert into grupos(grupoid, grupodesc) values(5, '1DAW-G2')")
            obtenerConexion().createStatement().executeUpdate("insert into grupos(grupoid, grupodesc) values(6, '1DAW-G3')")
        }


    }




    override fun crearGrupo(grupo: Grupo) {
        val sql = "INSERT INTO GRUPOS(grupodesc) VALUES(?)"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, grupo.grupodesc)
                stmt.executeUpdate()
                grupo

                val generatedKeys = stmt.generatedKeys
                if (generatedKeys.next()) {
                    val id = generatedKeys.getInt(1)
                    grupo.grupoid = id
                }
            }
        }
    }

    override fun obtenerGrupo(id: Int): Grupo? {
        val sql2 = "SELECT * FROM GRUPOS WHERE grupoid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql2).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Grupo(
                        rs.getInt("grupoid"),
                        rs.getString("grupodesc"),
                        rs.getInt("mejorposCTFid")
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun actualizarMejorPosCtf(grupo: Grupo) {
        val sql3 = "SELECT CTFid, puntuacion FROM CTFS WHERE grupoid=? ORDER BY puntuacion DESC LIMIT 1"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql3).use { stmt ->
                stmt.setInt(1, grupo.grupoid)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val mejorCTFid = rs.getInt("CTFid")
                    grupo.mejorPosCTFid = mejorCTFid
                    val updateStmt = obtenerConexion().prepareStatement(
                        """
                        UPDATE GRUPOS SET mejorposCTFid=? WHERE grupoid=?
                    """.trimIndent()
                    )
                    updateStmt.setInt(1, mejorCTFid)
                    updateStmt.setInt(2, grupo.grupoid)
                    updateStmt.executeUpdate()
                    grupo
                }
            }
        }
    }

    override fun eliminarGrupo(id: Int) {
        val sql4 = "DELETE FROM GRUPOS WHERE grupoid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql4).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
        }
    }

    override fun obtenerTodosGrupos(): List<Grupo> {
        val sql5 = "SELECT * FROM GRUPOS"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql5).use { stmt ->
                val rs = stmt.executeQuery()
                val grupos = mutableListOf<Grupo>()
                while (rs.next()) {
                    grupos.add(
                        Grupo(
                            rs.getInt("grupoid"),
                            rs.getString("grupodesc"),
                            rs.getInt("mejorposCTFid")
                        )
                    )
                }
                grupos
            }
        }
    }

    override fun eliminarTodosGrupos() {
        val sql6 = "DELETE FROM GRUPOS"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql6).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }


    // Aquí empieza las consultas de la tabla CTFS
    override fun anadirCtf(ctf: CTF) {
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

    override fun obtenerParticipacionCtf(id: Int, grupoid: Int): CTF? {
        val sql2 = "SELECT * FROM CTFS WHERE CTFid=? AND grupoid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql2).use { stmt ->
                stmt.setInt(1, id)
                stmt.setInt(2, grupoid)
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

    override fun eliminarCtf(id: Int, grupoid: Int) {
        val sql4 = "DELETE FROM CTFS WHERE CTFid=? AND grupoid=?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql4).use { stmt ->
                stmt.setInt(1, id)
                stmt.setInt(2, grupoid)
                stmt.executeUpdate()
            }
        }
    }

    override fun obtenerTodosCtfs(): List<CTF> {
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