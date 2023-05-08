import dao.GrupoCtfDAO
import dao.entity.CTF
import logs.i
import services.GrupoCtfServiceImpl
import sql_utils.DataSourceFactory


fun main(args: Array<String>) {


    val participaciones = listOf(
        CTF(1, 1, 3),
        CTF(1, 2, 101),
        CTF(2, 2, 3),
        CTF(2, 1, 50),
        CTF(2, 3, 1),
        CTF(3, 1, 50),
        CTF(3, 3, 5)
    )
    val mejoresCtfByGroupId = calculaMejoresResultados(participaciones)
    println(mejoresCtfByGroupId)



    // Creamos la instancia de la base de datos
    val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)

    // Creamos la instancia de GrupoCtfDAO
    val grupoCtfDao = GrupoCtfDAO(dataSource)

    // Creamos la instancia de GrupoCtfServiceImplementación
    val grupoCtfService = GrupoCtfServiceImpl(grupoCtfDao)




    // Operaciones

    /*
    Ejemplo:

    Añadir participación:

    args[0] = "-a"
    args[1] = 1
    args[2] = 2
    args[3] = 100
    */


    if (args.isEmpty()) {
        println("ERROR: El número de parametros no es adecuado.")
    }

    when (args[0]) {
        // Operación 1
        "-a" -> {
            if(args.size != 4) {
                println("ERROR: El número de parametros no es adecuado.")
            }
            else {
                val ctfid = args[1].toInt()
                val grupoid = args[2].toInt()
                val puntuacion = args[3].toInt()

                // Obtenemos el grupo que pertenece el grupoid
                val grupo = grupoCtfService.obtenerGrupo(grupoid)

                if (grupo != null) {
                    /* Comprobamos si ya exista una participación del grupo en el Ctf
                     */
                    val existeCtf = grupoCtfService.obtenerParticipacionCtf(ctfid, grupoid) != null

                    if (existeCtf) {
                        println("ERROR: Ya existe una participación del grupo $grupoid en el CTF $ctfid.")
                    }
                    else {

                        // Creamos si no existe el nuevo Ctf para insertarlo en la tabla
                        val nuevoCtf = CTF(ctfid, grupoid, puntuacion)
                        grupoCtfService.anadirCtf(nuevoCtf)

                        /* Actualizar el campo mejorposCTFid de cada grupo en la tabla GRUPOS iterando
                           sobre cada uno.

                         */
                        val grupos = grupoCtfService.obtenerTodosGrupos()
                        for (cadaGrupo in grupos) {
                            grupoCtfService.actualizarMejorPosCtf(cadaGrupo)
                        }

                        i("grupoCtfService.anadirCtf", "Procesado: Añadida participación del grupo $grupoid en" +
                                "el CTF $ctfid con una puntuación de $puntuacion puntos.")

                        println("Procesado: Añadida participación del grupo $grupoid en el CTF $ctfid con una puntuación de $puntuacion puntos.")
                    }

                }
                else {
                    println("ERROR: El grupo $grupoid no existe.")
                }


            }
        }

        // Operación 2
        "-d" -> {
            if (args.size != 3) {
                println("ERROR: El número de parámetros no es adecuado.")
            }

            else {
                val ctfid = args[1].toInt()
                val grupoid = args[2].toInt()

                grupoCtfService.eliminarCtf(ctfid, grupoid)

                i("grupoCtfService.eliminarCtf", "Procesado: Eliminada participación del grupo $grupoid"+
                        " en el CTF $ctfid.")

                println("Procesado: Eliminada participación del grupo $grupoid en el CTF $ctfid.")

                /* Actualizar el campo mejorposCTFid de cada grupo en la tabla GRUPOS iterando
               sobre cada uno.

                */
                val grupos = grupoCtfService.obtenerTodosGrupos()
                for (cadaGrupo in grupos) {
                    grupoCtfService.actualizarMejorPosCtf(cadaGrupo)
                }

            }

        }

        // Operación 3
        "-l" -> {
            if (args.size > 2) {
                println("ERROR: EL número de parámetros no es adecuado.")
            }
            val grupoid = if (args.size == 2) args[1].toInt() else null

            if (grupoid == null) {
                // Listado de todos los grupos de la tabla
                val grupos = grupoCtfService.obtenerTodosGrupos()
                println("Procesado: Listado de todos los grupos: ")

                i("grupoCtfService.obtenerTodosGrupos", "Procesado: Listado de todos los grupos:")

                for (grupo in grupos) {
                    println("Grupo: ${grupo.grupoid}  ${grupo.grupodesc}  MejorCTF: ${grupo.mejorPosCTFid}")
                }
            }
            else {
                // Listado de una fila de un grupo concreto
                val grupo = grupoCtfService.obtenerGrupo(grupoid)
                if (grupo == null) {
                    println("ERROR: No se ha encontrado el grupo con el id: $grupoid")
                } else {
                    i("grupoCtfService.obtenerGrupo", "Grupo: ${grupo.grupoid}  ${grupo.grupodesc} " +
                            "MejorCTF: ${grupo.mejorPosCTFid}")

                    println("Procesado: Listado participación del grupo: ${grupo.grupodesc}")
                    println("Grupo: ${grupo.grupoid}  ${grupo.grupodesc}  MejorCTF: ${grupo.mejorPosCTFid} ")
                }
            }
        }

        else -> {
            println("ERROR: Comando no reconocido.")
        }
    }




}



/**
 *
 *
 * @param participaciones
 * @return devuelve un mutableMapOf<Int, Pair<Int, Ctf>> donde
 *      Key: el grupoId del grupo
 *      Pair:
 *          first: Mejor posición
 *          second: Objeto CTF el que mejor ha quedado
 */
private fun calculaMejoresResultados(participaciones: List<CTF>): MutableMap<Int, Pair<Int, CTF>> {
    // Agrupamos las participaciones por CTF id y por grupoid
    val participacionesPorCTFId = participaciones.groupBy { it.ctfId }
    val participacionesPorGrupoId = participaciones.groupBy { it.grupoid }

    // Calculamos los mejores resultados de cada uno de los grupos en cada CTF
    val mejoresCtfsPorGrupoId = mutableMapOf<Int, Pair<Int, CTF>>()
    participacionesPorCTFId.values.forEach { ctfs ->
        val ctfsOrdenadosPorPuntuacion = ctfs.sortedByDescending { it.puntuacion }
        participacionesPorGrupoId.keys.forEach { grupoId ->
            val posicionNueva = ctfsOrdenadosPorPuntuacion.indexOfFirst { it.grupoid == grupoId }
            if (posicionNueva >= 0) {
                val posicionMejor = mejoresCtfsPorGrupoId.getOrDefault(grupoId, null)
                if (posicionMejor != null) {
                    if (posicionNueva < posicionMejor.first)
                        mejoresCtfsPorGrupoId[grupoId] = Pair(posicionNueva, ctfsOrdenadosPorPuntuacion[posicionNueva])
                } else
                    mejoresCtfsPorGrupoId[grupoId] = Pair(posicionNueva, ctfsOrdenadosPorPuntuacion[posicionNueva])

            }
        }
    }
    return mejoresCtfsPorGrupoId
}