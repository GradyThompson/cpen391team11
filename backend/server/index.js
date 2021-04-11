const express = require('express');
const app = express();

const { MongoClient } = require('mongodb');
const CONNECTION_URL = 'mongodb+srv://testboy:dudwotkrhks2@cluster0.i6ov7.mongodb.net/myFirstDatabase?retryWrites=true&w=majority'
const client = new MongoClient(CONNECTION_URL, { useUnifiedTopology: true });
client.connect();

var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

const ffmpegPath = require('@ffmpeg-installer/ffmpeg').path;
var ffmpeg = require("fluent-ffmpeg");
ffmpeg.setFfmpegPath(ffmpegPath);

const multer = require('multer');
const path = require('path');
const mstorage = multer.diskStorage({
	destination: (req, file, cb) => {
		cb(null, 'uploads/')
	},
	filename: (req, file, cb) => {
		cb(null, file.originalname)
	},
});
const upload = multer({storage: mstorage});

var admin = require('firebase-admin');
var serviceAccount = require('./firebasecpen391.json');
admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
	storageBucket: 'macro-dogfish-306517.appspot.com'
});
var bucket =  admin.storage().bucket();



app.get('/', (req, res) => {
    res.send('We are connected');
});

app.post('/auth/create', (req, response) => {
	// check is user_id is already in db, return error
	// connect to auth db and update with user_id and password
	if (req.body.name == undefined || req.body.email == undefined) {
		response.status(400).send({ "message": "Create new user failed due to request fields not being valid" });
	}
	else {
		var userDB = client.db("smarti").collection("user");
		userDB.findOne({ Email: req.body.email }, function (err, item) {
			if (err) {
				throw err;
			}
			/**
			 * If user does not exist, we have to insert the user into the DB
			 */
			if (item == undefined) {
				userDB.insertOne({
					Name: req.body.name,
					Email: req.body.email
				}, function (err) {
					if (err) {
						response.status(400).send({ "message": "Error occured when creating user" }, 400);
						throw err;
					}
					else {
						response.status(200).send({ success: true });
					}
				});
			}
			else {
				// User was already created
				response.status(200).send({ success: true });
			}
		});
	}
});

app.post('/save', (req, response) => {

	var inFile = "vtest.mpeg";
	var outFile = "test1.mp4";

	console.log("Got request... start converting");

	const spawn = require("child_process").spawn;
	const mt = spawn('python', ['./motion_detector.py', 'vtest.mpeg']);

	mt.stdout.on('data', (data) => {
		console.log(data.toString());
		response.send(data);
	});

	mt.stderr.on('data', (data) => {

	});

	ffmpeg(inFile).save(outFile);

	// if (req.body.email == undefined || req.body.path == undefined || req.body.filename == undefined || req.body.date == undefined) {
	// 	response.status(400).send({ "message": "fields are not valid" });
	// } else {
	// 	var fileDB = client.db("smarti").collection("file");
	// 	fileDB.insertOne({
	// 		Email: req.body.email,
	// 		Path: req.body.path,
	// 		FileName: req.body.filename,
	// 		Date: req.body.date
	// 	}, function (err) {
	// 		if (err) {
	// 			response.status(400).send({ "message": "Error occured when saving file info" }, 400);
	// 			throw err;
	// 		}
	// 		else {
	// 			response.status(200).send({ success: true });
	// 		}
	// 	});
	// }
});

app.get('/getVid', (req, response) => {
	var fileDB = client.db("smarti").collection("file");
	fileDB.find({}, {projection: {_id: 0}}).toArray(function(error, documents) {
		if (error) throw error;
	
		response.send(documents);
	});
	
});

app.post('/uploadvideo', upload.single('video'), (req, response, next) => {
	console.log(req.file);

	var inFile = path.parse(req.file.filename);
	var outFile = 'uploads/' + inFile.name + '.mp4';

	console.log("Got request... start converting");

	ffmpeg(req.file.path)
	.save(outFile)
	.on('end', function() {
		console.log('processing done');
		bucket.upload(outFile, function(err, file, apiResponse) {
			if (err) {
				console.log('upload error');
			}
	
		});
	})

	response.send("successful");
});



app.listen(3000, () => console.log('listening on port 3000'));