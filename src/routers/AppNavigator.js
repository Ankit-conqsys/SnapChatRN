import { createStackNavigator, createAppContainer } from "react-navigation";
import ShareOptions from "../components/ShareOptions/ShareOptions";
import Share from "../components/Share/Share";


const AppNavigator = createStackNavigator({
    ShareOptions: {
        screen: ShareOptions
    },
    Share: {
        screen: Share
    }
});

export default createAppContainer(AppNavigator);
