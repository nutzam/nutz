var ioc = {
	dataSource : {
		comment :"This is Apache common Data Source object",
		type :"org.apache.commons.dbcp.BasicDataSource",
		singleton :true,
		parent :null,
		lifecycle : {
			depose :"close"
		},
		args : [],
		fields : {
			driverClassName :"org.postgresql.Driver",
			url :"jdbc:postgresql://localhost:5432/zmole",
			username :"admin",
			password :"admin"
		}
	},
	dao : {
		comment :"Dao object",
		type :"org.nutz.dao.impl.NutDao",
		singleton :true,
		parent :null,
		args : [ {
			refer :"dataSource"
		}, {
			id :0,
			comment :null,
			name :"sqls",
			type :"org.nutz.dao.impl.FileSqlManager",
			singleton :true,
			parent :null,
			args : [ "sqls/zmole.sqls" ],
			fields : {
				keys : {
					java :"org.nutz.fake.FakeKeys.keys"
				}
			}
		} ],
		fields : {}
	},
	colors : {
		comment :null,
		type :null,
		singleton :true,
		parent :null,
		lifecycle : {
			depose :"org.nutz.fake.ColorDeposer"
		},
		args : [ {
			refer :"dao"
		}, {
			disk :"org.nutz/mole/Mole.class"
		} ],
		fields : {
			output : {
				env :"ZZH_OUTPUT_DIR"
			},
			indexFile : {
				file :"file/index.o"
			}
		}
	},
	reds : {
		comment :null,
		type :"org.nutz.fake.RedService",
		singleton :true,
		parent :"colors",
		args : [],
		fields : {
			array : [ "A", "B" ],
			jspFile : {
				jsp :"jsp.main.show"
			},
			callback : {
				java :"org.nutz.Static.name"
			}
		}
	},
	blues : {
		type :"org.nutz.fake.BlurService",
		singleton :true,
		parent :"colors",
		args : [ null ],
		fields : {
			setting : {
				x :45,
				y :78,
				width :100,
				height :49,
				map : {
					a :10,
					b :false
				},
				oneFile : {
					file :"ttt.txt"
				}
			},
			disabled :true,
			refer :null
		}
	}

};