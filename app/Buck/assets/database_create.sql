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

CREATE TABLE Jobs (
	_id integer primary key autoincrement, 
	Name text
);

CREATE TABLE Mills (
	_id integer primary key autoincrement,
	Name text,
	Enabled integer
);

CREATE TABLE Prices (
	_id integer primary key autoincrement,
	MillId integer,
	Length integer,
	Rate integer, 
	Top integer, 
	Price integer
);

CREATE VIEW "job_totals" AS
	select JobId, count(JobId) as count from Cuts group by JobId;