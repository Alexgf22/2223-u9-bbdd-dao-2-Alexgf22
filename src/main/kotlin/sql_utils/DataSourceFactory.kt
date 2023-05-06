package sql_utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.jdbcx.JdbcDataSource
import java.io.PrintWriter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource

object DataSourceFactory {
    enum class DataSourceType {
        HIKARI,
        JDBC
    }

    fun getDS(dataSourceType: DataSourceType): DataSource {
        return when (dataSourceType) {
            DataSourceType.HIKARI -> {
                val config = HikariConfig()
                config.jdbcUrl = "jdbc:h2:./default"
                config.username = "user"
                config.password = "user"
                config.driverClassName = "org.h2.Driver"
                config.maximumPoolSize = 10
                config.isAutoCommit = true
                config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                val dataSource = HikariDataSource(config)
                dataSource.connection
                dataSource
            }

            DataSourceType.JDBC -> JdbcDataSource("jdbc:h2:./default", "user", "user")
        }
    }


}



class JdbcDataSource(private val url: String, private val user: String, private val password: String) : DataSource {

    override fun getConnection(): Connection {
        return DriverManager.getConnection(url, user, password)
    }

    override fun getConnection(username: String?, password: String?): Connection {
        return connection
    }

    override fun getLoginTimeout(): Int {
        return 0
    }

    override fun getLogWriter(): PrintWriter {
        TODO("Not yet implemented")
    }

    override fun setLogWriter(out: PrintWriter?) {
        TODO("Not yet implemented")
    }

    override fun setLoginTimeout(seconds: Int) {
        // No-op
    }

    override fun getParentLogger(): java.util.logging.Logger {
        throw UnsupportedOperationException("Not supported")
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        throw SQLException("Not a wrapper")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        return false
    }

}