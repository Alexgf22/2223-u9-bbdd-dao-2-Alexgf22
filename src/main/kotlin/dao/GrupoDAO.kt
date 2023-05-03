package dao

import dao.entity.Grupo
import dao.interfaces.IGrupoDao
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
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
        val stmt = conexion.prepareStatement("""
            INSERT INTO GRUPOS(grupodesc) VALUES(?)
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS)
        stmt.setString(1, grupo.grupodesc)
        stmt.executeUpdate()

        val generatedKeys = stmt.generatedKeys
        if (generatedKeys.next()) {
            val id = generatedKeys.getInt(1)
            grupo.grupoid = id
        }

    }

    override fun obtenerGrupo(id: Int): Grupo? {
        val stmt = conexion.prepareStatement("""
            SELECT * FROM GRUPOS WHERE grupoid=?
        """.trimIndent())
        stmt.setInt(1, id)
        val rs = stmt.executeQuery()
        return if (rs.next()) {
            Grupo(
                rs.getInt("grupoid"),
                rs.getString("grupodesc"),
                rs.getInt("mejorposCTFid")
            )
        } else {
            null
        }

    }





    override fun actualizarMejorPosCtf(grupo: Grupo) {
        val stmt = conexion.prepareStatement("""
            SELECT CTFid, puntuacion FROM CTFS WHERE grupoid=? ORDER BY puntuacion DESC LIMIT 1
        """.trimIndent())
        stmt.setInt(1, grupo.grupoid)
        val rs = stmt.executeQuery()
        if (rs.next()) {
            val mejorCTFid = rs.getInt("CTFid")
            grupo.mejorPosCTFid = mejorCTFid
            val updateStmt = conexion.prepareStatement("""
                UPDATE GRUPOS SET mejorposCTFid=? WHERE grupoid=?
            """.trimIndent())
            updateStmt.setInt(1, mejorCTFid)
            updateStmt.setInt(2, grupo.grupoid)
            updateStmt.executeUpdate()
        }

    }




    override fun eliminarGrupo(id: Int) {
        val stmt = conexion.prepareStatement("""
        DELETE FROM GRUPOS WHERE grupoid=?
    """.trimIndent())
        stmt.setInt(1, id)
        stmt.executeUpdate()

    }

    override fun obtenerTodosGrupos(): List<Grupo> {
        val stmt = conexion.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM GRUPOS")
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
        return grupos

    }

    override fun eliminarTodosGrupos() {
        val stmt = conexion.prepareStatement("""
        DELETE FROM GRUPOS
    """.trimIndent())
        stmt.executeUpdate()

    }


}

