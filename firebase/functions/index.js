const functions = require("firebase-functions");
const https = require("https");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.location = functions.https.onRequest((request, response) => {
  let xOrigin = request.query.xOrigin;
  let yOrigin = request.query.yOrigin;
  let xDest = request.query.xDest;
  let yDest = request.query.yDest;

  let str_origin = "origin=" + xOrigin + "," + yOrigin;
  let str_dest = "destination=" + xDest + "," + yDest;

  const API_KEY = "AIzaSyAqSUM5njA-jN2iYxuSSbinWDPZ8fA_sso";

  const URL =
    "https://maps.googleapis.com/maps/api/directions/json?origin=" +
    str_origin +
    "&destination=" +
    str_dest +
    "&key=" +
    API_KEY;

  https
    .get(URL, resp => {
      let data = "";

      // A chunk of data has been recieved.
      resp.on("data", chunk => {
        data += chunk;
      });

      // The whole response has been received. Print out the result.
      resp.on("end", () => {
        console.log(data);
      });
    })
    .on("error", err => {
      console.log("Error: " + err.message);
    });
  response.send({ text: xOrigin });
});
