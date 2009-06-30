var ioc = {
	id :45,
	comment :"Hello Peter",
	name :"testObj",
	type :"org.nutz.ioc.meta.fake.FakeObject",
	singleton :false,
	parent :"fakeParent",
	lifecycle : {
		depose :"org.nutz.ioc.meta.fake.FakeDeposer"
	},
	args : [ "arg0", {
		config :"someParam"
	}, {
		id :0,
		comment :null,
		name :"testInner",
		type :"org.nutz.ioc.meta.fake.FakeInnerObject",
		singleton :true,
		parent :null,
		lifecycle : {
			depose :" "
		},
		args : [ "a", true, {
			env :"TOMCAT_HOME"
		} ],
		fields : {
			A :"AA"
		}
	} ],
	fields : {
		A :"a",
		B : {
			file :"/WEB-INF/web.xml"
		},
		C : [ "c0", null, {
			java :"org.nutz.ioc.meta.fake.Fake.id"
		}, 59 ],
		D : {
			M1 :105,
			M2 : {
				server :"attName"
			}
		}
	}
};