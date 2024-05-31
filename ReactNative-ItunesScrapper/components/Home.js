//TODO: Should allow to search for a music by artist name or track name.
import {Button, FlatList, Image, StyleSheet, Text, TextInput, TouchableOpacity, View} from "react-native";
import {useEffect, useState} from "react";


export default function Home({navigation}) {
	const trackData = require('../assets/trackData.json').results
	const [query, setQuery] = useState("");
	const [tracks, setTracks] = useState()
	const [favorite, setFavorite] = useState([])

	// Update favorites
	const toggleFavorite = (item) => {
		console.log("-------------------")
		console.log(favorite.length)
		console.log(favorite.includes(item))
		if (favorite.includes(item)) {
			console.info("remove")
			setFavorite(favorite.filter(fav => fav !== item))
		} else {
			console.info("add")
			setFavorite([...favorite, item])
		}
	}
	const updateFav = (favList) => {
		console.log(favList)
		setFavorite(favList)
	}

	// Check for update in the search bar.
	useEffect(() => {
		const doQueryToItunes = async () => {
			console.log(query)
			const queryUrl = "https://itunes.apple.com/search?media=music&term="+query
			console.log(queryUrl)
			await fetch(queryUrl)
				.then((response)=> response.json())
				.then(json =>{
					const data = json.results
					setTracks(data)
				})
				.catch((error) => {
				console.error(error)
			})
		}
		doQueryToItunes()
	}, [query]);

	return (
		<View style={styles.container}>
			<TextInput
				placeholder="Enter Track or Artist name."
				onChangeText={setQuery}
				value={query}
			/>
			<Text>{query}</Text>
			<Button title="Goto Fav" onPress={() => navigation.navigate("Playlist", {
				fav: favorite, cb: (favList) => {
					updateFav(favList)
				}
			})}/>
			{/*Result List*/}
			<FlatList data={tracks} keyExtractor={item => item.trackId}
								numColumns={1}
								style={{width: "95%" }}
								renderItem={({item}) =>
				<TouchableOpacity style={styles.listItem.container}
													onPress={() => navigation.navigate("Details", {track: item})}>
					<Image style={styles.listItem.image} source={{uri: item.artworkUrl30}}/>
					<View style={styles.listItem.data}>
						<Text style={styles.listItem.data.title}>Track:</Text>
						<Text style={styles.listItem.data.value}>{item.trackName}</Text>
						<Text style={styles.listItem.data.title}>Artist:</Text>
						<Text style={styles.listItem.data.value}>{item.artistName}</Text>
						<Text style={styles.listItem.data.title}>Album:</Text>
						<Text style={styles.listItem.data.value}>{item.collectionName}</Text>
					</View>
					{/*Fav Button*/}
					<Button color={favorite.includes(item) ? "red" : "blue"}
									title={favorite.includes(item) ? "remove to Fav" : "add to Fav"}
									onPress={() => toggleFavorite(item)}/>
				</TouchableOpacity>
			}/>
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
		button: {
			flex: 5,
			width: "100%",
			height: "100%",
			justifyContent: 'center'
		},
		data: {
			marginLeft: 10,
			marginRight: 10,
			flex: 6,
			flexWrap: 'wrap',
			flexDirection: 'row',
			title: {
				width: "25%",
				color: 'lightgray',
				fontWeight: "bold",
			},
			value: {
				width: "75%",
			},
			// width: "30%",
			// marginLeft: 10,
		}

	}
});