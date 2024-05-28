//TODO: Show details about the selected Music
import {Button, Image, StyleSheet, Text, TextInput, View} from "react-native";
import moment from 'moment'

export default function Details({route, navigation}) {
	const {track} = route.params
	const dateformat = require('dateformat');
	return (
		<View style={styles.container}>
			<Image style={styles.listItem.image} source={{uri: track.artworkUrl100}}/>
			<Text>{track.trackName}</Text>
			<Text>{track.artistName}</Text>
			<Text>{track.collectionName}</Text>
			<Text>Date: {dateformat(track.releaseDate,"dddd, mmmm dS, yyyy, h:MM:ss TT")}</Text>
			<Text>Durée: {Math.floor(track.trackTimeMillis / 60000)}"{Math.floor(track.trackTimeMillis / 1000) % 60}'{track.trackTimeMillis % 1000}</Text>
			<Text>{track.country}</Text>
			<Text>Genre: {track.primaryGenreName}</Text>
			<Text>Disc: {track.discNumber}/{track.discCount}</Text>
			<Text>Track: {track.trackNumber}/{track.trackCount}</Text>

			<Button title="Back" onPress={() => navigation.goBack()}/>
		</View>
	)
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: '#fff',
		alignItems: 'center',
		justifyContent: 'center',
	},

	listItem: {
		image: {height: 100, width: 100},
		data: {}
	}
});