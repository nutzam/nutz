var ioc = {
	dataSource : {
		comment :"This is Apache common Data Source object",
		type :"org.apache.commons.dbcp.BasicDataSource",
		singleton :true,
		parent :null,
		deposeby :"close",
		deposer :null,
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
		type :"com.zzh.dao.impl.NutDao",
		singleton :true,
		parent :null,
		deposeby :null,
		deposer :null,
		args : [ {
			refer :"dataSource"
		}, {
			id :0,
			comment :null,
			name :"sqls",
			type :"com.zzh.dao.impl.FileSqlManager",
			singleton :true,
			parent :null,
			deposeby :null,
			deposer :null,
			args : [ "sqls/zmole.sqls" ],
			fields : {
				keys : {
					java :"com.zzh.fake.FakeKeys.keys"
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
		deposeby :null,
		deposer :"com.zzh.fake.ColorDeposer",
		args : [ {
			refer :"dao"
		}, {
			disk :"com/zzh/mole/Mole.class"
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
		type :"com.zzh.fake.RedService",
		singleton :true,
		parent :"colors",
		deposeby :null,
		deposer :null,
		args : [],
		fields : {
			array : [ "A", "B" ],
			jspFile : {
				jsp :"jsp.main.show"
			},
			callback : {
				java :"com.zzh.Static.name"
			}
		}
	},
	blues : {
		type :"com.zzh.fake.BlurService",
		singleton :true,
		parent :"colors",
		deposeby :null,
		deposer :null,
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