package com.example.kode_viewmodel.model

interface AdapterItems

class Ordinary(id: String,
               avatarUrl: String,
               firstName: String,
               lastName: String,
               userTag: String,
               department: String,
               position: String,
               birthday: String,
               phone: String
) : Person.Items(id, avatarUrl, firstName, lastName, userTag, department, position, birthday, phone),
    AdapterItems


class Birthday(id: String,
               avatarUrl: String,
               firstName: String,
               lastName: String,
               userTag: String,
               department: String,
               position: String,
               birthday: String,
               phone: String
) : Person.Items(id, avatarUrl, firstName, lastName, userTag, department, position, birthday, phone),
    AdapterItems

class Separator(val year: String) : AdapterItems

class Skeleton: AdapterItems