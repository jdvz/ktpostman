package com.novardis.test.inject

class Injector {
    companion object {
        private val INSTANCE = Injector()

        fun <T: Bean> inject(clazz: Class<T>): T {
            return INSTANCE.inject(clazz)
        }
    }

    val beans = HashMap<Class<Bean>, Bean>()

    fun <T:Bean> inject(clazz: Class<T>): T {
        var bean: Bean? = retrieveBean(clazz as Class<Bean>)
        if (bean == null) {
            bean = clazz.newInstance()
            beans.put(clazz, bean)
        }
        return bean as T
    }

    fun retrieveBean(clazz: Class<Bean>): Bean? {
        return beans.get(clazz)
    }
}

interface Bean {

}
