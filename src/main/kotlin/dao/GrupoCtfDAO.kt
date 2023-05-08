package dao

import dao.entity.CTF
import dao.entity.Grupo
import dao.interfaces.ICtfDao
import dao.interfaces.IGrupoDao
import java.sql.Connection
import javax.sql.DataSource

/**
 * @property dataSource: DataSource  propiedad de acceso a datos  que se utiliza para configurar una conexión a una base de datos
 * Esta clase implementa las interfaces tanto para la tabla Ctfs como la tabla
 * Grupos. Realiza en primer lugar una conexión con la base de datos, después
 * en el inicializador crea ambas tablas con las restricciones de clave
 * primaria, clave foránea correspondientes y también inserta varios registros
 * en la tabla Grupos. Tras el init se sobreescriben las funciones de las interfaces
 * cada una con su comportamiento correspondiente según a que tabla haga
 * referencia y la operación que tenga que hacer: insertar, obtener, actualizar, borrar...
 *
 */
class GrupoCtfDAO(private val dataSource: DataSource): IGrupoDao, ICtfDao {

    /**
     * La función obtener conexión intenta obtenerla haciendo uso de la propiedad
     * dataSource.
     * @return Connection devuelve la conexión a la base de datos.
     */
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


    /**
     * La función crearGrupo realiza primero una consulta para insertar
     * en la tabla grupos un nuevo registro. El valor de grupodesc se obtiene
     * del objeto grupo que se le pasa por parámetro. Después se realiza una
     * conexión a la base de datos. A continuación se prepara la consulta con
     * el método prepareStatement. Dicho método devuelve un objeto PreparedStatement
     * que se usa para ejecutar la consulta con el método executeUpdate. Después
     * se actualiza el objeto grupo con el valor que tiene el campo grupoid que
     * se obtiene con el método generatedKeys que devuelve un objeto ResultSet
     * que contiene los valores de las claves generadas.
     */
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


    /**
     *
     */
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
                stmt.setInt(1, ctf.ctfId)
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
                stmt.setInt(3, ctf.ctfId)
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