DROP TABLE IF EXISTS Cuts;
CREATE TABLE Cuts (
	_id integer primary key autoincrement, 
	JobId integer, 
	MillId integer,
	PriceId integer, 
	Width integer, 
	Length integer, 
	FBM integer, 
	Value integer
);

DROP TABLE IF EXISTS Jobs;
CREATE TABLE Jobs (
	_id integer primary key autoincrement, 
	Name text
);

DROP TABLE IF EXISTS Mills;
CREATE TABLE Mills (
	_id integer primary key autoincrement,
	Name text,
	Enabled integer
);

DROP TABLE IF EXISTS Prices;
CREATE TABLE Prices (
	_id integer primary key autoincrement,
	MillId integer,
	Length integer,
	Rate integer, 
	Top integer, 
	Price integer
);

DROP VIEW IF EXISTS Job_Totals;
CREATE VIEW Job_Totals AS
	select JobId, count(JobId) as count from Cuts group by JobId;