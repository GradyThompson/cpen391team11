import mongoose from 'mongoose';
const { Schema } = mongoose;

const user = new Schema({
  Uid:  Number,
  Token: String,
  Threshold: Number,
  Notification: Boolean,
});

const files = new Schema({
    Uid: Number,
    Url: String,
    Date: String,
    Length: String,
    Severity: Number
});