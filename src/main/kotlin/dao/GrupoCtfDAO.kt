package dao

import dao.entity.CTF
import dao.entity.Grupo
import dao.interfaces.ICtfDao
import dao.interfaces.IGrupoDao
import logs.i
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
     * del objeto grupo que se le pasa por parámetro.
     * @param grupo: Grupo  objeto de la clase Grupo que contiene varios atributos
     * Después se realiza una
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
            i("GrupoCtfDAO.crearGrupo", "Preparing statement")
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, grupo.grupodesc)
                i("GrupoCtfDAO.crearGrupo", "Executing query")
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
     * Esta función recibe como parámetro un id.
     * @param id de tipo Int.
     * Se realiza una consulta a la base de datos para obtener los datos del grupo con el id
     * especificado con la propiedad dataSource para obtener una conexión a la base
     * de datos y hacer la consulta.
     * Se crea un objeto PreparedStatement con la consulta y se establece el valor del parámetro id
     * en el índice 1. Después se ejecuta la consulta con el método executeQuery() y se
     * guarda el resultado en un objeto ResultSet.
     *
     * El objeto Grupo se crea con los valores de las columnas 'grupoid', 'grupodesc' y
     * 'mejorposCTFid' del registro obtenido en la consulta.
     *
     * @return Grupo?  devuelve un objeto Grupo con los datos obtenidos en la consulta si el objeto
     * ResultSet tiene mínimo un registro, sino, devuelve null.
     */
    override fun obtenerGrupo(id: Int): Grupo? {
        val sql2 = "SELECT * FROM GRUPOS WHERE grupoid=?"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.obtenerGrupo", "Preparing statement")
            conn.prepareStatement(sql2).use { stmt ->
                stmt.setInt(1, id)
                i("GrupoCtfDAO.obtenerGrupo", "Executing query")
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


    /**
     * @param grupo: Grupo  objeto de la clase Grupo.
     *
     * La función actualizarMejorPosCtf lo que hace actualizar la propiedad mejorPosCTFid del objeto Grupo y la guarda en
     * la base de datos. Primero, se realiza una consulta en la base de datos para obtener el CTF con la
     * puntuación más alta del grupo especificado. Después, se actualiza la propiedad mejorPosCTFid del
     * objeto Grupo con el id del CTF encontrado y se actualiza la fila correspondiente en la base de datos.
     * @return grupo  se retorna el objeto Grupo actualizado
     */
    override fun actualizarMejorPosCtf(grupo: Grupo) {
        val sql3 = "SELECT CTFid, puntuacion FROM CTFS WHERE grupoid=? ORDER BY puntuacion DESC LIMIT 1"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.actualizarMejorPosCtf", "Preparing statement")
            conn.prepareStatement(sql3).use { stmt ->
                stmt.setInt(1, grupo.grupoid)
                i("GrupoCtfDAO.actualizarMejorPosCtf", "Executing query")
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


    /**
     * @param id: Int identificador del Grupo.
     * Se elimina un grupo de la base de datos mediante la ejecución de una sentencia SQL que borra la
     * fila correspondiente a ese grupo en la tabla GRUPOS. Por lo que se utiliza un
     * objeto PreparedStatement que se prepara con la sentencia SQL y se asigna el valor del parámetro
     * id al marcador de posición ? en la sentencia. Después se ejecuta la consulta y se borra la fila
     * perteneciente al grupo. La función no devuelve nada, ya que solamente elimina el
     * grupo de la base de datos.
     *
     */
    override fun eliminarGrupo(id: Int) {
        val sql4 = "DELETE FROM GRUPOS WHERE grupoid=?"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.eliminarGrupo", "Preparing statement")
            conn.prepareStatement(sql4).use { stmt ->
                stmt.setInt(1, id)
                i("GrupoCtfDAO.eliminarGrupo", "Executing query")
                stmt.executeUpdate()
            }
        }
    }


    /**
     * Se obtiene una lista de todos los grupos existentes en la base de datos. Posteriormente, se realiza una
     * consulta SQL que selecciona todos los registros de la tabla "GRUPOS". Después, se recorre el ResultSet
     * para crear objetos Grupo a partir de los datos obtenidos en la consulta y se agregan a una lista mutable.
     * @return List<Grupo>  se retorna la lista de grupos creada.
     */
    override fun obtenerTodosGrupos(): List<Grupo> {
        val sql5 = "SELECT * FROM GRUPOS"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.obtenerTodosGrupos", "Preparing statement")
            conn.prepareStatement(sql5).use { stmt ->
                i("GrupoCtfDAO.obtenerTodosGrupos", "Executing query")
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


    /**
     * Esta función lo que hace es eliminar todos los registros de la tabla GRUPOS en la base de datos
     * mediante la ejecución de una consulta SQL, conectándose a la base de datos, preparando y ejecutando
     * la consulta.
     * @return Unit
     */
    override fun eliminarTodosGrupos() {
        val sql6 = "DELETE FROM GRUPOS"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.eliminarTodosGrupos", "Preparing statement")
            conn.prepareStatement(sql6).use { stmt ->
                i("GrupoCtfDAO.eliminarTodosGrupos", "Executing query")
                stmt.executeUpdate()
            }
        }
    }


    // Aquí empieza las consultas de la tabla CTFS

    /**
     * @param ctf: CTF objeto de la clase CTF.
     * Lo que hace la función es:  recibir un objeto de tipo CTF, crea una consulta SQL que inserta
     * los datos del CTF en la tabla "CTFS", y después ejecuta la consulta mediante la propiedad
     * dataSource para obtener una conexión a la base de datos.
     * @return CTF retorna el objeto CTF que se recibió como entrada.
     */
    override fun anadirCtf(ctf: CTF) {
        val sql = "INSERT INTO CTFS(CTFid, grupoid, puntuacion) VALUES(?, ?, ?)"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.anadirCtf", "Preparing statement")
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, ctf.ctfId)
                stmt.setInt(2, ctf.grupoid)
                stmt.setInt(3, ctf.puntuacion)
                i("GrupoCtfDAO.anadirCtf", "Executing query")
                stmt.executeUpdate()
                ctf
            }
        }
    }


    /**
     * @param id: Int  identificador del Ctf.
     * @param grupoid: Int  Identificador del grupo que participa en el CTF.
     *
     * En primer lugar, recibe dos parámetros, id y grupoid.
     * La función busca en la base de datos una participación que tenga el id y el grupoid especificados
     * @return CTF  objeto de la clase CTF con los datos que corresponden, sino, devuelve null.
     */
    override fun obtenerParticipacionCtf(id: Int, grupoid: Int): CTF? {
        val sql2 = "SELECT * FROM CTFS WHERE CTFid=? AND grupoid=?"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.obtenerParticipacionCtf", "Preparing statement")
            conn.prepareStatement(sql2).use { stmt ->
                stmt.setInt(1, id)
                stmt.setInt(2, grupoid)
                i("GrupoCtfDAO.obtenerParticipacionCtf", "Executing query")
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


    /**
     * @param ctf: CTF instancia de la clase CTF.
     *
     * La función lo que hace es actualizar la información de un CTF existente en la base de datos.
     * Primero, se define la consulta SQL que actualiza la información del CTF. Después se utiliza
     * el objeto dataSource para obtener una conexión a la base de datos, se prepara
     * la consulta SQL mediante la conexión obtenida y se asignan los valores de los parámetros
     * a la consulta. A continuación, se ejecuta la consulta y se actualiza el objeto ctf que se
     * ha pasado como parámetro.
     * @return ctf  retorna el objeto ctf actualizado.
     *
     */
    override fun actualizarCtf(ctf: CTF) {
        val sql3 = "UPDATE CTFS SET grupoid=?, puntuacion=? WHERE CTFid=?"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.actualizarCtf", "Preparing statement")
            conn.prepareStatement(sql3).use { stmt ->
                stmt.setInt(1, ctf.grupoid)
                stmt.setInt(2, ctf.puntuacion)
                stmt.setInt(3, ctf.ctfId)
                i("GrupoCtfDAO.actualizarCtf", "Executing query")
                stmt.executeUpdate()
                ctf
            }
        }
    }


    /**
     * @param id: Int
     * @param grupoid: Int
     *
     * La función lo que hace es eliminar un registro de la tabla CTFS de la base de datos
     * que tenga el CTFid y grupoid especificados como parámetros. Se prepara un statement SQL
     * con el comando DELETE y se le asignan los valores de los parámetros. Después se ejecuta el
     * statement y se elimina el registro correspondiente.
     */
    override fun eliminarCtf(id: Int, grupoid: Int) {
        val sql4 = "DELETE FROM CTFS WHERE CTFid=? AND grupoid=?"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.eliminarCtf", "Preparing statement")
            conn.prepareStatement(sql4).use { stmt ->
                stmt.setInt(1, id)
                stmt.setInt(2, grupoid)
                i("GrupoCtfDAO.eliminarCtf", "Executing query")
                stmt.executeUpdate()
            }
        }

    }




    /**
     * La función lo que hace es devolver una lista de todos los CTFs registrados en la base de datos.
     * Primero se realiza la consulta SQL para seleccionar todos los registros de la tabla CTFS.
     * Después se ejecuta la consulta y se itera a través del ResultSet para extraer cada fila y
     * construir un objeto CTF con los valores de esa fila.
     * @return List<CTF> se retorna una lista con los objetos CTF agregados anteriormente.
     */
    override fun obtenerTodosCtfs(): List<CTF> {
        val sql5 = "SELECT * FROM CTFS"
        return dataSource.connection.use { conn ->
            i("GrupoCtfDAO.obtenerTodosCtfs", "Preparing statement")
            conn.prepareStatement(sql5).use { stmt ->
                i("GrupoCtfDAO.obtenerTodosCtfs", "Executing query")
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