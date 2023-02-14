package com.example.kode_viewmodel.model

import com.example.kode_viewmodel.vm.AppViewModel

interface IRow

 open class ABC(val id: String,
          val avatarUrl: String,
          val firstName: String,
          val lastName: String,
          val userTag: String,
          val department: String,
          val position: String,
          val birthday: String,
          val phone: String): IRow


class Birthday(id: String,
               avatarUrl: String,
               firstName: String,
               lastName: String,
               userTag: String,
               department: String,
               position: String,
               birthday: String,
               phone: String)
    :ABC(id, avatarUrl, firstName, lastName, userTag, department, position, birthday, phone), IRow

class Separator(val year: String): IRow

class Skeleton: IRow