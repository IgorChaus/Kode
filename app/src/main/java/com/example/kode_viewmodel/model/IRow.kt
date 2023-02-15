package com.example.kode_viewmodel.model

import com.example.kode_viewmodel.vm.AppViewModel

interface IRow

class ABC(id: String,
               avatarUrl: String,
               firstName: String,
               lastName: String,
               userTag: String,
               department: String,
               position: String,
               birthday: String,
               phone: String)
    :Person.Items(id, avatarUrl, firstName, lastName, userTag, department, position, birthday, phone)
    ,IRow


class Birthday(id: String,
               avatarUrl: String,
               firstName: String,
               lastName: String,
               userTag: String,
               department: String,
               position: String,
               birthday: String,
               phone: String)
    :Person.Items(id, avatarUrl, firstName, lastName, userTag, department, position, birthday, phone)
    ,IRow

class Separator(val year: String): IRow

class Skeleton: IRow