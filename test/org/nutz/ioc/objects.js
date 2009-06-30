var ioc = {
	/*--------------------------------------------------------------------*/
	b1 : {
		type :"org.nutz.ioc.JsonIocTest$B",
		fields : {
			id :11,
			name :"b1",
			ioc : {
				refer :"@ioc"
			}
		}
	},
	b2 : {
		type :"org.nutz.ioc.JsonIocTest$B",
		fields : {
			id :22,
			name :"b2"
		}
	},
	/*--------------------------------------------------------------------*/
	a1 : {
		type :"org.nutz.ioc.JsonIocTest$A",
		args : [ "org.nutz.lang.meta.Email" ],
		fields : {
			field :"account"
		}
	},
	/*--------------------------------------------------------------------*/
	a2 : {
		type :"org.nutz.ioc.JsonIocTest$A",
		args : [ "org.nutz.lang.meta.Email" ],
		fields : {
			field :"host"
		}
	},
	/*--------------------------------------------------------------------*/
	a3 : {
		type :"org.nutz.ioc.JsonIocTest$A",
		args : [ "org.nutz.lang.meta.Email" ],
		fields : {
			field :"account",
			bs : [ {
				id :11,
				name :"b1",
				ioc : {refer:"@ioc"}
			}, {
				id :22,
				name :"b2",
				ioc : {refer:"@ioc"}
			} ]
		}
	},
	/*--------------------------------------------------------------------*/

	a4 : {
		type :"org.nutz.ioc.JsonIocTest$A",
		args : [ "org.nutz.lang.meta.Email" ],
		fields : {
			field :"account",
			bs : [ "9:f1", "10:uu" ]
		}
	},
	/*--------------------------------------------------------------------*/

	a5 : {
		type :"org.nutz.ioc.JsonIocTest$A",
		args : [ "org.nutz.lang.meta.Email" ],
		fields : {
			field :"account",
			bs : [ {
				refer :"b1"
			}, {
				refer :"b2"
			}, {
				refer :null
			} ]
		}
	},
	/*--------------------------------------------------------------------*/

	"b-misc" : {
		type :"org.nutz.ioc.JsonIocTest$B",
		fields : {
			id :15,
			name :"b-misc",
			objs : [ "ss", 23, {
				type :"org.nutz.lang.meta.Email",
				args : [ "zozoh@263.net" ]
			}, {
				refer :"a1"
			} ]
		}
	},
	/*--------------------------------------------------------------------*/
	"ioc-file1" : {
		type :"org.nutz.ioc.JsonIocTest$IocFile",
		args : [ "org.nutz/ioc" ]
	},
	/*--------------------------------------------------------------------*/
	o1 : {
		fields : {
			name : {
				java :"org.nutz.ioc.JsonIocTest.oName"
			}
		}
	},
	/*--------------------------------------------------------------------*/
	c1 : {
		type :"org.nutz.ioc.JsonIocTest$C",
		args : [ {
			type :"org.nutz.lang.meta.Email",
			args : [ "abc@263.net" ]
		} ]
	},
	c2 : {
		type :"org.nutz.ioc.JsonIocTest$C",
		fields : {
			email : {
				type :"org.nutz.lang.meta.Email",
				args : [ "abc@263.net" ]
			}
		}
	},
	/*--------------------------------------------------------------------*/
	d1 : {
		fields : {
			map : {
				cc1 : {
					refer :"c1"
				},
				cc2 : {
					refer :"c2"
				}
			}
		}
	},
	/*--------------------------------------------------------------------*/

	seasonFruit : {
		fields : {
			onSale :true
		}
	},
	expiredFruit : {
		fields : {
			onSale :false
		}
	},
	/*--------------------------------------------------------------------*/
	apple : {
		extends :"seasonFruit",
		fields : {
			name :"Apple",
			price :4
		}
	},
	guoguang : {
		extends :"apple",
		fields : {
			price :3
		}
	},
	strawberry : {
		extends :"expiredFruit",
		fields : {
			name :"Strawberry",
			price :15
		}
	},
	/*--------------------------------------------------------------------*/
	durian : {
		fields : {
			alias :"Durian",
			cost :34
		}
	}
/*--------------------------------------------------------------------*/
};