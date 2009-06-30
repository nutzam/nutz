var ioc = {
	/*--------------------------------------------------------------*/
	red : {
		type :"org.nutz.dao.test.meta.Base",
		fields : {
			name :"Red",
			country : {
				refer :"China"
			},
			platoons : {
				DF : {
					refer :"DF"
				},
				seals : {
					refer :"seals"
				}
			}
		}
	},
	/*--------------------------------------------------------------*/
	China : {
		type :"org.nutz.dao.test.meta.Country",
		singleton :false,
		fields : {
			name :"China"
		}
	},
	/*--------------------------------------------------------------*/
	blue : {
		type :"org.nutz.dao.test.meta.Base",
		lifecycle : {
			depose :"org.nutz.ioc.BaseDeposer"
		},
		fields : {
			name :"Blue",
			country : {
				refer :"US"
			},
			platoons : {
				DF : {
					refer :"DF"
				},
				DF2 : {
					refer :"DF"
				}
			}
		}
	},
	/*--------------------------------------------------------------*/
	green : {
		type :"org.nutz.dao.test.meta.Base",
		lifecycle : {
			depose :"org.nutz.ioc.BaseDeposer"
		},
		fields : {
			name :"Green",
			country : {
				refer :"US"
			},
			platoons : {
				DF : {
					refer :"DF"
				},
				seals : {
					refer :"seals"
				}
			}
		}
	},
	/*--------------------------------------------------------------*/
	US : {
		type :"org.nutz.dao.test.meta.Country",
		lifecycle : {
			depose :"org.nutz.ioc.CountryDeposer"
		},
		fields : {
			name :"United States"
		}
	},
	/*--------------------------------------------------------------*/
	DF : {
		type :"org.nutz.dao.test.meta.Platoon",
		fields : {
			name :"DF"
		}
	},
	/*--------------------------------------------------------------*/
	seals : {
		type :"org.nutz.dao.test.meta.Platoon",
		singleton :false,
		fields : {
			name :"seals"
		}
	},
	/*--------------------------------------------------------------*/
	SF : {
		base : {
			type :"org.nutz.dao.test.meta.Base"
		}
	}
};