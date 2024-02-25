import React, { useState, useRef, useEffect } from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import Ionicons from "react-native-vector-icons/Ionicons";
import HomeScreen from "./HomeScreen";
import WordList from "./WordLearn/WordList";
import FactCheck from "./FactCheck";
import LearnScreen from "./Learn";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { StyleSheet, SafeAreaView, Platform, StatusBar } from "react-native";
import { getFocusedRouteNameFromRoute } from "@react-navigation/native";
import MyPage from "./MyPage";
import LoginScreen from "./Login";
import SignupScreen from "./Singup";
import BeforeHome from "./beforeHome";
import AsyncStorage from "@react-native-async-storage/async-storage";

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [accessToken, setAccessToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);

  const [showBeforeHome, setShowBeforeHome] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      setShowBeforeHome(true);

      const timer = setTimeout(() => {
        setShowBeforeHome(false);
      }, 4000);

      return () => clearTimeout(timer);
    }
  }, [isAuthenticated]);

  const getTabBarVisibility = (route) => {
    const routeName = getFocusedRouteNameFromRoute(route) ?? "Home";

    if (routeName === "Quiz" || routeName === "Results") {
      return "none";
    } else {
      return "flex";
    }
  };

  const MainTabNavigator = () => (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName;
          if (route.name === "Home") {
            iconName = focused ? "home" : "home-outline";
          } else if (route.name === "Word") {
            iconName = focused ? "book" : "book-outline";
          } else if (route.name === "FactCheck") {
            iconName = focused ? "play-circle" : "play-circle-outline";
          } else if (route.name === "MyPage") {
            iconName = focused ? "person-circle" : "person-circle-outline";
          }
          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: "#EB5929",
        tabBarInactiveTintColor: "white",
        tabBarStyle: {
          backgroundColor: "#55433B",
          display: getTabBarVisibility(route),
        },
      })}
    >
      <Tab.Screen
        name="Home"
        component={HomeStack}
        options={{ headerShown: false }}
        initialParams={{ accessToken, refreshToken }}
      />
      <Tab.Screen
        name="FactCheck"
        component={FactCheckStack}
        options={{ headerShown: false }}
        initialParams={{ accessToken, refreshToken }}
      />
      <Tab.Screen
        name="Word"
        component={WordList}
        options={{ headerShown: false }}
        initialParams={{ accessToken, refreshToken }}
      />
      <Tab.Screen
        name="MyPage"
        component={MyPage}
        options={{ headerShown: false }}
        initialParams={{ accessToken, refreshToken }}
      />
    </Tab.Navigator>
  );

  const AuthStackNavigator = () => (
    <Stack.Navigator>
      <Stack.Screen name="Login" options={{ headerShown: false }}>
        {(props) => (
          <LoginScreen
            {...props}
            setIsAuthenticated={setIsAuthenticated}
            setAccessToken={setAccessToken}
            setRefreshToken={setRefreshToken}
          />
        )}
      </Stack.Screen>
      <Stack.Screen
        name="Signup"
        component={SignupScreen}
        options={{
          headerShown: true,
          headerStyle: {
            backgroundColor: "#EB5929",
            justifyContent: "center",
          },
          headerTitleStyle: {
            color: "white",
          },
        }}
      />
    </Stack.Navigator>
  );

  return (
    <SafeAreaView style={styles.container}>
      <NavigationContainer>
        {isAuthenticated ? (
          <>
            {showBeforeHome ? (
              <BeforeHome />
            ) : (
              <MainTabNavigator
                accessToken={accessToken}
                refreshToken={refreshToken}
              />
            )}
          </>
        ) : (
          <AuthStackNavigator />
        )}
      </NavigationContainer>
    </SafeAreaView>
  );
}

function HomeStack({ navigation, route }) {
  const { accessToken, refreshToken } = route.params;

  React.useLayoutEffect(() => {
    const routeName = getFocusedRouteNameFromRoute(route) ?? "Home";
    if (routeName === "Check words & Summarize") {
      navigation.setOptions({
        tabBarStyle: {
          display: "none",
        },
      });
    } else {
      navigation.setOptions({
        tabBarStyle: {
          display: "flex",
          backgroundColor: "#55433B",
        },
      });
    }
  }, [navigation, route]);

  return (
    <Stack.Navigator>
      <Stack.Screen
        name="홈화면"
        component={HomeScreen}
        options={{ headerShown: false }}
        initialParams={{ accessToken, refreshToken }}
      />
      <Stack.Screen
        name="Check words & Summarize"
        component={LearnScreen}
        initialParams={{ accessToken, refreshToken }}
        options={{
          headerShown: true,
          tabBarStyle: { display: "none" },
          headerStyle: {
            backgroundColor: "#EB5929",
            justifyContent: "center",
          },
          headerTitleStyle: {
            color: "white",
          },
        }}
      />
    </Stack.Navigator>
  );
}

function FactCheckStack({ navigation, route }) {
  const { accessToken, refreshToken } = route.params;

  React.useLayoutEffect(() => {
    const routeName = getFocusedRouteNameFromRoute(route) ?? "Home";
    if (routeName === "Check words & Summarize") {
      navigation.setOptions({
        tabBarStyle: {
          display: "none",
        },
      });
    } else {
      navigation.setOptions({
        tabBarStyle: {
          display: "flex",
          backgroundColor: "#55433B",
        },
      });
    }
  }, [navigation, route]);

  return (
    <Stack.Navigator>
      <Stack.Screen
        name="Factcheck"
        component={FactCheck}
        options={{ headerShown: false }}
        initialParams={{ accessToken, refreshToken }}
      />
      <Stack.Screen
        name="Check words & Summarize"
        component={LearnScreen}
        initialParams={{ accessToken, refreshToken }}
        options={{
          headerShown: true,
          tabBarStyle: { display: "none" },
          headerStyle: {
            backgroundColor: "#EB5929",
            justifyContent: "center",
          },
          headerTitleStyle: {
            color: "white",
            fontWeight: "bold",
          },
        }}
      />
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export default App;
