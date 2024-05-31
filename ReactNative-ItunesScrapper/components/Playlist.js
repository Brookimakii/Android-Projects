//TODO: Show the Playlist of favorite Music.
import {Button, FlatList, Image, StyleSheet, Text, TextInput, TouchableOpacity, View} from "react-native";
import {useState} from "react";
import Home from "./Home";


export default function Playlist({route, navigation}) {
	const {fav} = route.params
	const [favorite, setFavorite] = useState(fav)
	const removeFav = (item) => {
		setFavorite(favorite.filter(fav => fav !== item))
		route.params.cb(favorite.filter(fav => fav !== item))
	}
	// Fav List
	return (
		<View style={styles.container}>
			{favorite.length === 0 ?
				<Text>No Music is selected as "favorite"</Text>
				:
				<FlatList data={favorite} keyExtractor={item => item.trackId}
									numColumns={1}
									style={{width: "95%" }}
									renderItem={({item}) =>
					<TouchableOpacity style={styles.listItem.container} onPress={() => navigation.navigate("Details", {track: item})}>
						<Image style={styles.listItem.image} source={{uri: item.artworkUrl30}}/>
						<View style={styles.listItem.data}>
							<Text style={styles.listItem.data.title}>Track:</Text>
							<Text style={styles.listItem.data.value}>{item.trackName}</Text>
							<Text style={styles.listItem.data.title}>Artist:</Text>
							<Text style={styles.listItem.data.value}>{item.artistName}</Text>
							<Text style={styles.listItem.data.title}>Album:</Text>
							<Text style={styles.listItem.data.value}>{item.collectionName}</Text>
						</View>
						<Button style={styles.listItem.button} color="red" title="remove to Fav" onPress={() => removeFav(item)}/>
					</TouchableOpacity>
				}/>
			}
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
		container: {
			flex: 1,
			// justifyContent: 'center',
			alignItems: "center",
			overflow: "hidden",
			borderWidth: 1,
			flexDirection: 'row',
			borderColor: 'gray',
			marginBottom: 10,
			justifyContent: 'center',
			// width: "90%",
			// justifyContent: "flex-end"
			// flex: 1,
			// alignSelf: 'stretch',
			// textAlign: 'left',
			// justifyContent: 'center',
			// justifyContent: 'center',
			// alignItems: 'left'
		},
		image: {
			height: 30,
			width: 30,
			justifyContent: 'center'
		},
		button:{
			flex: 5,
			width: 150,
			height: "100%",
			justifyContent: 'center'
		},
		data: {
			marginLeft: 10,
			flex: 6,
			flexWrap: 'wrap',
			flexDirection: 'row',
			title:{
				width: "25%",
				color: 'lightgray',
				fontWeight: "bold",
			},
			value:{
				width: "75%",
			},
			// width: "30%",
			// marginLeft: 10,
		}

	}
});