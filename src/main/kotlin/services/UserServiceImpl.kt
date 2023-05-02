package services

import dao.CtfDAO
import java.util.*

class UserServiceImpl(private val userDao: CtfDAO) : UserService {
    override fun create(user: UserEntity): UserEntity {
        return userDao.create(user)
    }

    override fun getById(id: UUID): UserEntity? {
        return userDao.getById(id)
    }

    override fun update(user: UserEntity): UserEntity {
        return userDao.update(user)
    }

    override fun delete(id: UUID) {
        userDao.delete(id)
    }

    override fun getAll(): List<UserEntity> {
        return userDao.getAll()
    }
}