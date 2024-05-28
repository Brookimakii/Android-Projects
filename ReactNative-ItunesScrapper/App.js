import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import {createNativeStackNavigator} from "@react-navigation/native-stack";
import {NavigationContainer} from "@react-navigation/native";
import Home from "./components/Home"
import Details from "./components/Details"
import Playlist from "./components/Playlist"

export default function App() {
  const Stack = createNativeStackNavigator()


  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Home">
        <Stack.Screen name="Home" component={Home}/>
        <Stack.Screen name="Details" component={Details}/>
        <Stack.Screen name="Playlist" component={Playlist}/>
      </Stack.Navigator>
    </NavigationContainer>
    // <View style={styles.container}>
    //   <Text>Open up App.js to start working on your app!</Text>
    //   <StatusBar style="auto" />
    // </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
