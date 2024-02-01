const express = require("express");
const app = express();
const mongo = require("mongodb").MongoClient;
const mongoDbUrl = "mongodb://localhost:27017";

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

let trapsCollection; 

mongo.connect(
  mongoDbUrl,
  {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  },
  (err, client) => {
    if (err) {
      console.error(err);
      return;
    }
    const db = client.db("snmp");
    trapsCollection = db.collection("traps");
  }
);

app.get("/traps", (req, res) => {
  if (!trapsCollection) {
    res.status(500).json({ error: "Database not initialized" });
    return;
  }

  trapsCollection.find().toArray((err, items) => {
    if (err) {
      console.error(err);
      res.status(500).json({ err: err });
      return;
    }
    res.status(200).json({ traps: items });
  });
});

app.listen(3030, () => console.log("(MongoDB) Server is running at port 3030"));
