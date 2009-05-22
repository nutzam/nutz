var ioc = {
	/*--------------------------------------------------------------*/
	red : {
		type :"com.zzh.dao.test.meta.Base",
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
		type :"com.zzh.dao.test.meta.Country",
		singleton :false,
		fields : {
			name :"China"
		}
	},
	/*--------------------------------------------------------------*/
	blue : {
		type :"com.zzh.dao.test.meta.Base",
		deposer :"com.zzh.ioc.BaseDeposer",
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
		type :"com.zzh.dao.test.meta.Base",
		deposer :"com.zzh.ioc.BaseDeposer",
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
		type :"com.zzh.dao.test.meta.Country",
		deposer :"com.zzh.ioc.CountryDeposer",
		fields : {
			name :"United States"
		}
	},
	/*--------------------------------------------------------------*/
	DF : {
		type :"com.zzh.dao.test.meta.Platoon",
		fields : {
			name :"DF"
		}
	},
	/*--------------------------------------------------------------*/
	seals : {
		type :"com.zzh.dao.test.meta.Platoon",
		singleton :false,
		fields : {
			name :"seals"
		}
	},
	/*--------------------------------------------------------------*/
	SF : {
		base : {
			type :"com.zzh.dao.test.meta.Base"
		}
	}
};