import mongoose from 'mongoose';
const { Schema } = mongoose;

const user = new Schema({
  Uid:  Number,
  Threshold: Number,
  Notification: Boolean
});

const file = new Schema({
    Uid: Number,
    Url: String,
    Date: String,
    Length: String,
    Severity: Number
});