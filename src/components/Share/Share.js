import React, { Component } from 'react';
import { StyleSheet, Text, View, TextInput, Image } from 'react-native';
import { TouchableWithoutFeedback, ScrollView } from 'react-native-gesture-handler';
import RNSnapKit from '../RNSnapKit';
import Toast from '../Toast';
import states from './Share.state';
import styles from './styles';

// const instructions = Platform.select({
//   ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
//   android:
//     'Double tap R on your keyboard to reload,\n' +
//     'Shake or press menu button for dev menu',
// });

export default class Share extends Component {
    static navigationOptions = {
        title: 'Share Snap',
        headerTitleStyle: { color: '#FFFFFF', },
        headerStyle: { backgroundColor: '#2196F3' },
        headerTintColor: 'white',
    };

    constructor(props) {
        super(props);
        this.state = states;
    }

    pickImages = () => {
        RNSnapKit.pickImage((response) => {
            let pickedImage = {
                uri: 'file://' + response.uri
            };
            this.setState({ image: pickedImage });
        });
    }

    sendSnap = () => {
        this.state.image.uri ? 
        RNSnapKit.send(this.state.caption, this.state.attachmentUrl, (response) => {
            console.log('test');
        }) :
        Toast.showMessage('Please upload an image');
    }

    componentWillUnmount = () => {
        this.state = states;
    }



    render() {
        return (
            <View style={styles.container}>
                <ScrollView style={{width: '100%'}} contentContainerStyle={styles.scrollContainer}
                    showsHorizontalScrollIndicator={false}
                    showsVerticalScrollIndicator={false}>
                    <View style={styles.container}>
                        <TouchableWithoutFeedback
                            style={styles.button}
                            onPress={() => this.pickImages()}>
                            <Text style={styles.buttonLabel}>Add Image</Text>
                        </TouchableWithoutFeedback>
                        {this.state.image.uri ?
                            <Image style={styles.imageView}
                                source={{ uri: this.state.image.uri }} /> :
                            <View style={[styles.imageView, styles.imagePlaceholderView]}>
                                <Text style={styles.imagePlaceholderText}>
                                    Add Image
                                    </Text>
                            </View>}
                        <TextInput
                            style={styles.textInput}
                            placeholder={"Text to share here"}
                            onChangeText={(text) => this.setState({ caption: text })}
                            value={this.state.text} />
                    </View>
                </ScrollView>
                <TouchableWithoutFeedback
                    style={styles.shareButton}
                    onPress={() => this.sendSnap()}>
                    <Text style={styles.buttonLabel}>Share to Snapchat</Text>
                </TouchableWithoutFeedback>
            </View>
        );
    }
}

