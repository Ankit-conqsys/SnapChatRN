import React, { Component } from 'react';
import { Text, View } from 'react-native';
import {TouchableWithoutFeedback} from 'react-native-gesture-handler';
import RNSnapKit from '../RNSnapKit';
import Toast from '../Toast';
import styles from './styles';


class ShareOptions extends Component {
    static navigationOptions = {
        header: null
    };

    shareToSnap = () => {
        RNSnapKit.hasSnapAccess(result => {
            result ? this.props.navigation.navigate('Share') 
            : this.loginSnapchat()
        })
    }

    loginSnapchat = () => {
        RNSnapKit.loginSnapchat(isLoggedIn => {
            isLoggedIn ? this.props.navigation.navigate('Share') 
            : Toast.showMessage('Login Failed')
        })
    }

    render() {
        return (
            <View style={styles.container}>
                <TouchableWithoutFeedback style={styles.button}
                onPress={() => this.shareToSnap()}>
                    <Text style={styles.buttonLabel}>Share to Snap</Text>
                </TouchableWithoutFeedback>
                <TouchableWithoutFeedback style={styles.button}
                onPress={() => Toast.showMessage("Coming Soon")}>
                    <Text style={styles.buttonLabel}>Share to Instagram</Text>
                </TouchableWithoutFeedback>
            </View>
        );
    }
}

export default ShareOptions;