package com.example.kode_viewmodel.model

import com.example.kode_viewmodel.vm.AppViewModel

interface IRow

class ABC(val id: String,
          val avatarUrl: String,
          val firstName: String,
          val lastName: String,
          val userTag: String,
          val department: String,
          val position: String,
          val birthday: String,
          val phone: String): IRow


class Birthday(val id: String,
               val avatarUrl: String,
               val firstName: String,
               val lastName: String,
               val userTag: String,
               val department: String,
               val position: String,
               val birthday: String,
               val phone: String) : IRow

class Separator(val year: String): IRow

class Skeleton: IRow