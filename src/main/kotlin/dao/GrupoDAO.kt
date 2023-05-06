package dao

import dao.entity.Grupo
import dao.interfaces.IGrupoDao
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource

class GrupoDAO(private val dataSource: DataSource): IGrupoDao {

    private val conexion: Connection = DriverManager.getConnection("jdbc:h2:mem:test")

    init {
        conexion.createStatement().executeUpdate("""
            CREATE TABLE IF NOT EXISTS GRUPOS (
                grupoid INT NOT NULL AUTO_INCREMENT,
                grupodesc VARCHAR(100) NOT NULL,
                mejorposCTFid INT,
                PRIMARY KEY (grupoid)
            )
        """.trimIndent())
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
                    val updateStmt = conexion.prepareStatement(
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


}

