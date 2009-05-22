var ioc = {
	id :45,
	comment :"Hello Peter",
	name :"testObj",
	type :"com.zzh.ioc.meta.fake.FakeObject",
	singleton :false,
	parent :"fakeParent",
	deposeby :"close",
	deposer :"com.zzh.ioc.meta.fake.FakeDeposer",
	args : [ "arg0", {
		config :"someParam"
	}, {
		id :0,
		comment :null,
		name :"testInner",
		type :"com.zzh.ioc.meta.fake.FakeInnerObject",
		singleton :true,
		parent :null,
		deposeby :null,
		deposer :null,
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
			java :"com.zzh.ioc.meta.fake.Fake.id"
		}, 59 ],
		D : {
			M1 :105,
			M2 : {
				server :"attName"
			}
		}
	}
};