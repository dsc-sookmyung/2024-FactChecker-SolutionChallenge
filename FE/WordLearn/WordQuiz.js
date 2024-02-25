import React, { useState, useEffect, useCallback } from "react";
import {
  View,
  Text,
  Button,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  ImageBackground,
} from "react-native";
import { useFocusEffect } from "@react-navigation/native";

const dummy_words = [
  {
    id: 1,
    quiz_word: "단어1",
    mean: "뜻1",
    knowStatus: false,
  },
  {
    id: 2,
    quiz_word: "단어2",
    mean: "뜻2",
    knowStatus: false,
  },
  {
    id: 3,
    quiz_word: "단어3",
    mean: "뜻3",
    knowStatus: false,
  },
  {
    id: 4,
    quiz_word: "단어4",
    mean: "뜻4",
    knowStatus: false,
  },
];

export function QuizHomeScreen({ navigation, accessToken, refreshToken }) {
  const [quizTaken, setQuizTaken] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {}, [loading]);

  const fetchWords = async () => {
    setLoading(true);

    try {
      const response = await fetch(
        "http://34.22.87.117:5000/study/daily-quiz/quiz-word",
        {
          method: "GET",
          headers: {
            Accept: "application/json",
          },
        }
      );

      if (!response.ok) {
        alert("Error getting words.. please retry");
        throw new Error(`Network response was not ok: ${response.status}`);
      }

      const json = await response.json();

      if (!json.words[0] || !json.words[0].quiz_word || !json.words[0].mean) {
        alert("Error getting words.. please retry");
        throw new Error("quiz_word or mean key is missing in the response");
      }

      console.log("Homescreen");
      console.log(json);
      navigation.navigate("Quiz", { fetchedWords: json.words });
    } catch (error) {
      console.error("Error fetching words:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={[styles.container, { backgroundColor: "white" }]}>
      <Text style={styles.dailyQuiz}>Daily Quiz</Text>
      <View style={styles.explainBox}>
        <Text style={styles.explain}>Here's a daily quiz for you,</Text>
        <Text style={styles.explain}>consisting of today's</Text>
        <Text style={styles.explain}>
          quite a bit challenging vocabularies.
        </Text>
        <Text style={styles.explain}>
          Take a Quiz Challenge and Level Up!💪🔥
        </Text>
        <Text style={styles.explain}>
          The more you get it right, the more you earn💰
        </Text>
        <Text style={styles.explain}>Plus! donate to those in need💸</Text>
      </View>
      {loading ? (
        <>
          <TouchableOpacity
            disabled={quizTaken}
            style={styles.bannedQuiz}
            activeOpacity={1}
          >
            <Text style={styles.banned}>Loading</Text>
            <Text style={styles.banned}>Words..</Text>
          </TouchableOpacity>
          <Text style={styles.bannedText}>Wait for a sec..</Text>
        </>
      ) : quizTaken ? (
        <>
          <TouchableOpacity
            disabled={quizTaken}
            style={styles.bannedQuiz}
            activeOpacity={1}
          >
            <Text style={styles.banned}>1 chance</Text>
            <Text style={styles.banned}>/ 100₩</Text>
          </TouchableOpacity>
          <Text style={styles.bannedText}>No more chances left!</Text>
          <TouchableOpacity>
            <View style={styles.payButton}>
              <Text style={styles.payButtonText}>Get more chances!</Text>
            </View>
          </TouchableOpacity>
        </>
      ) : (
        <TouchableOpacity
          onPress={fetchWords}
          disabled={quizTaken}
          style={styles.startQuiz}
        >
          <ImageBackground
            source={require("../assets/img/startQuiz.png")}
            style={styles.buttonImage}
            imageStyle={{ borderRadius: 150 }}
          >
            <Text style={styles.startQuizbuttonText}>START</Text>
            <Text style={styles.chanceLeftText}>Chances left : 1</Text>
          </ImageBackground>
        </TouchableOpacity>
      )}

      <View style={styles.marginBox}></View>
    </View>
  );
}

export function QuizScreen({ route, navigation, setShowSwiperButtons }) {
  const { fetchedWords } = route.params;

  const [quizWords, setQuizWords] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [remainingTime, setRemainingTime] = useState(8);
  const [userAnswers, setUserAnswers] = useState([]);
  const [options, setOptions] = useState([]);

  useEffect(() => {
    setQuizWords(fetchedWords);
  }, []);

  useFocusEffect(
    React.useCallback(() => {
      setShowSwiperButtons(false);
      return () => {
        setShowSwiperButtons(true);
      };
    }, [])
  );

  const currentWord =
    currentQuestionIndex < quizWords.length
      ? quizWords[currentQuestionIndex]
      : null;

  const getRandomMeans = () => {
    if (!currentWord || quizWords.length === 0) {
      return [];
    }

    let means = quizWords.map((word) => word.mean);
    means = means.filter((mean) => mean !== currentWord.mean);
    means.sort(() => 0.5 - Math.random());
    const selectedMeans = means.slice(0, 3);
    selectedMeans.push(currentWord.mean);
    selectedMeans.sort(() => 0.5 - Math.random());

    return selectedMeans;
  };

  const handleAnswer = (answer) => {
    const isCorrect = answer === currentWord.mean;
    const updatedWords = quizWords.map((word) =>
      word.id === currentWord.id ? { ...word, knowStatus: isCorrect } : word
    );
    setQuizWords(updatedWords);
    setUserAnswers([...userAnswers, { id: currentWord.id, isCorrect }]);

    if (currentQuestionIndex < quizWords.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
      setRemainingTime(8);
    } else {
      setRemainingTime(0);
    }
  };

  const remainingQuestions = quizWords.length - currentQuestionIndex;

  useEffect(() => {
    const timer = setInterval(() => {
      setRemainingTime((remainingTime) => remainingTime - 1);
    }, 1000);

    if (remainingTime === 0) {
      if (currentQuestionIndex < quizWords.length - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
        setRemainingTime(8);
      } else {
        navigation.navigate("Results", { quizWords: quizWords });
      }
    }
    return () => clearInterval(timer);
  }, [remainingTime, currentQuestionIndex, navigation]);

  useEffect(() => {
    setQuizWords(fetchedWords);
  }, [fetchedWords]);

  useEffect(() => {
    setOptions(getRandomMeans());
  }, [currentQuestionIndex, quizWords]);

  if (!currentWord || quizWords.length === 0) {
    return (
      <View style={styles.container}>
        <Text>Loading...</Text>
      </View>
    );
  }

  return (
    <View style={[styles.container, { backgroundColor: "white" }]}>
      <Text style={styles.remainingTime}>Time left: {remainingTime}</Text>
      <Text style={styles.remainingTime}>
        Questions left: {remainingQuestions}
      </Text>
      <View style={styles.wordContainer}>
        <Text style={styles.wordText}>{currentWord.quiz_word}</Text>
      </View>
      {options.map((option, index) => (
        <TouchableOpacity
          key={index}
          style={styles.button}
          onPress={() => handleAnswer(option)}
        >
          <Text style={styles.buttonText}>{option}</Text>
        </TouchableOpacity>
      ))}
      <View style={styles.marginQuizBox}></View>
    </View>
  );
}

// Results Screen component
export function ResultsScreen({
  route,
  navigation,
  accessToken,
  refreshToken,
}) {
  const [words, setWords] = useState(null);
  const [totalScore, setTotalScore] = useState(null);

  const getDayOfWeek = () => {
    const today = new Date();

    const dayOfWeek = today.getDay();

    const dayNames = ["일", "월", "화", "수", "목", "금", "토"];
    return dayNames[dayOfWeek];
  };

  useEffect(() => {
    const saveScore = async () => {
      try {
        const day = getDayOfWeek();
        console.log(day);
        console.log(totalScore);

        const response = await fetch(
          "http://35.216.92.188:8080/api/study/daily-quiz/score",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              ACCESS_TOKEN: `Bearer ${accessToken}`,
              REFRESH_TOKEN: refreshToken,
            },
            body: JSON.stringify({
              day: day,
              score: totalScore,
            }),
          }
        );

        console.log(response);

        const json = await response.json();

        if (response.ok) {
          console.log(json);
        } else {
          console.error("ResultsScreen : Error fetching words");
        }
      } catch (error) {
        console.error(error);
      }
    };

    if (totalScore != null) {
      console.log(totalScore);
      saveScore();
    }
  }, [totalScore, accessToken, refreshToken]);

  useEffect(() => {
    if (route.params?.quizWords) {
      const quizWords = route.params.quizWords;
      setWords(quizWords);
      const calculatedScore = calculateTotalScore(quizWords);
      setTotalScore(calculatedScore);
    }
  }, [route.params]);

  console.log(words);
  const toggleMeanVisibility = (id) => {
    const updatedWords = words.map((word) => {
      if (word.id === id) {
        return { ...word, showMean: !word.showMean };
      }
      return word;
    });
    setWords(updatedWords);
  };

  const calculateTotalScore = (words) => {
    return words.reduce((total, word) => total + (word.knowStatus ? 1 : 0), 0);
  };

  const saveWords = async () => {
    try {
      const dictionaryList = words
        .filter((item) => !item.knowStatus)
        .map((item) => ({
          word: item.quiz_word,
          mean: item.mean,
        }));

      const response = await fetch(
        "http://35.216.92.188:8080/api/learn/words",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ACCESS_TOKEN: `Bearer ${accessToken}`,
            REFRESH_TOKEN: refreshToken,
          },
          body: JSON.stringify({
            words: dictionaryList,
          }),
        }
      );

      const json = await response.json();

      if (response.ok) {
        console.log(json);
        navigation.navigate("QuizHome");
      } else {
        console.error("ResultsScreen : Error fetching words");
      }
    } catch (error) {
      console.error(error);
    }
  };

  const renderItem = ({ item }) => (
    <View style={{ flex: 1, backgroundColor: "white" }}>
      <View style={styles.resultWordContainer}>
        <View style={styles.resultRowContainer}>
          <TouchableOpacity onPress={() => toggleMeanVisibility(item.id)}>
            <Text style={styles.resultWord}>{item.quiz_word}</Text>
          </TouchableOpacity>
          <Text style={item.knowStatus ? styles.checkMark : styles.crossMark}>
            {item.knowStatus ? "✔" : "❌"}
          </Text>
        </View>
        {item.showMean && (
          <View style={styles.meanContainer}>
            <Text style={styles.mean}>{item.mean}</Text>
          </View>
        )}
      </View>
    </View>
  );

  return (
    <View style={[styles.resultContainer, { backgroundColor: "white" }]}>
      <Text style={styles.scoreText}>Total Score: {totalScore} / 20</Text>
      <Text style={styles.resultExplain}>You can check the meaning </Text>
      <Text style={styles.resultExplain}>by clicking on the word</Text>
      <FlatList
        data={words}
        renderItem={renderItem}
        keyExtractor={(item) => item.id.toString()}
      />
      <View style={styles.restartContainer}>
        <TouchableOpacity style={styles.button} onPress={saveWords}>
          <Text style={styles.buttonText}>Go Back & Save Words</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  dailyQuiz: {
    fontSize: 35,
    color: "#55433B",
    fontWeight: "bold",
  },
  explainBox: {
    marginVertical: 10,
    marginBottom: 10,
  },
  explain: {
    fontSize: 16,
    textAlign: "center",
  },
  startQuiz: {
    backgroundColor: "#ff7e54",
    padding: 10,
    borderRadius: 150,
    alignItems: "center",
    justifyContent: "center",
    margin: 10,
    width: 200,
    height: 200,
  },
  startQuizbuttonText: {
    fontSize: 30,
    color: "#55433B",
    fontWeight: "500",
    textAlign: "center",
    top: "110%",
  },
  chanceLeftText: {
    fontSize: 20,
    color: "#55433B",
    fontWeight: "500",
    textAlign: "center",
    top: "113%",
  },
  bannedQuiz: {
    backgroundColor: "#ff7e54",
    padding: 10,
    borderRadius: 150,
    alignItems: "center",
    justifyContent: "center",
    margin: 10,
    width: 200,
    height: 200,
    opacity: 0.5,
  },
  banned: {
    fontSize: 25,
    color: "#55433B",
    fontWeight: "500",
    textAlign: "center",
  },
  bannedText: {
    fontSize: 25,
    color: "#55433B",
    fontWeight: "500",
    textAlign: "center",
  },
  buttonImage: {
    width: "100%",
    height: "100%",
    resizeMode: "cover",
  },
  disabledButton: {
    backgroundColor: "#ffc2ad",
    padding: 10,
    borderRadius: 5,
    borderRadius: 150,
    alignItems: "center",
    justifyContent: "center",
    margin: 10,
    width: 200,
    height: 200,
  },
  marginBox: {
    minHeight: "17%",
  },
  payButton: {
    minWidth: "70%",
    backgroundColor: "#ff7e54",
    marginTop: 20,
    paddingVertical: 10,
    borderRadius: 15,
  },
  payButtonText: {
    textAlign: "center",
    color: "white",
    borderRadius: 20,
    fontWeight: "500",
    fontSize: 18,
  },
  remainingTime: {
    fontSize: 20,
    color: "#55433B",
    fontWeight: "500",
    marginBottom: 10,
  },
  button: {
    backgroundColor: "#ff7e54",
    padding: 10,
    margin: 8,
    borderRadius: 5,
    minWidth: "80%",
    maxWidth: "80%",
    marginTop: 10,
  },
  buttonText: {
    color: "white",
    fontSize: 15,
    textAlign: "center",
  },
  marginQuizBox: {
    height: "7%",
  },
  wordContainer: {
    backgroundColor: "white",
    borderRadius: 20,
    padding: 10,
    margin: 10,
    width: "80%",
    height: "30%",
    alignItems: "center",
    justifyContent: "center",
    elevation: 5,
  },
  wordText: {
    fontSize: 20,
    fontWeight: "bold",
  },
  rowContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  resultWordContainer: {
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  resultRowContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  resultWord: {
    fontSize: 18,
    minWidth: "90%",
  },
  resultMeanContainer: {
    backgroundColor: "#f0f0f0",
    padding: 10,
    borderRadius: 5,
    marginTop: 10,
  },
  resultMean: {
    fontSize: 16,
    fontStyle: "italic",
    color: "gray",
  },
  checkMark: {
    color: "green",
    fontSize: 20,
  },
  crossMark: {
    color: "red",
    fontSize: 20,
  },
  wordBox: {
    flexDirection: "row",
    justifyContent: "space-between",
    padding: 10,
    alignItems: "center",
  },
  resultContainer: {
    flex: 1,
  },
  restartContainer: {
    marginBottom: 60,
    alignItems: "center",
    justifyContent: "center",
  },
  checkMark: {
    color: "green",
    fontSize: 20,
  },
  crossMark: {
    color: "red",
    fontSize: 20,
  },
  meanContainer: {
    backgroundColor: "#f0f0f0",
    padding: 10,
    borderRadius: 5,
    marginTop: 10,
  },
  mean: {
    fontSize: 16,
    fontStyle: "italic",
    color: "gray",
  },
  scoreText: {
    fontSize: 25,
    textAlign: "center",
    marginVertical: 15,
  },
  resultExplain: {
    textAlign: "center",
    fontSize: 15,
    marginBottom: 3,
  },
});
