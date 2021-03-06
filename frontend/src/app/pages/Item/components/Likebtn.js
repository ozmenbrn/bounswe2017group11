import React from 'react';
import $ from 'jquery';
import PropTypes from 'prop-types';

/**
 *  Component for liking an item
 * 
 */
class Like extends React.Component {
  static propTypes = {
         /** Current Heritage Item */
         item: PropTypes.object,
         /** Login status of the user */
         loginStatus: PropTypes.bool
    }
    constructor(props){
      super(props);
      this.state = {
         count: "",
         isLiked: ""
       };
      this.clickHandler = this.clickHandler.bind(this)

   }
   render() {
      return(
        <div>
        <div class="likeContainer" >
        <a class="button">
        <i id="id1" ref="myComponentDiv" class={(this.state.isLiked) ? "fa fa-thumbs-o-up fa-3x text-info" : "fa fa-thumbs-o-up fa-3x text-muted"} aria-hidden="true"></i></a>
        <h4>{this.state.count} likes </h4>
        </div>
        </div>
      );
  }


  componentWillReceiveProps(nextProps){


    this.setState({
      count: nextProps.item.rate,
      isLiked: nextProps.item.is_rated
    });
  }

  componentWillMount(){

  }
  /**
   *  Handler method for liking an item.
   *
   *  Checks if user is logged in. If user likes the item sends the information to server through a POST call.
   *
   *  If the item is unliked, decreases the like count and updates the server with a POST call.
   *
   *  If user is not logged in alerts user to login in order to like the item.
   *  @public
   */
  clickHandler() {
      var clk = this.state.count
      var add = 0;
      if(!this.state.isLiked && this.props.loginStatus == "1"){
        add = 1;
        this.setState({
          isLiked: true
        });

         var myHeaders = new Headers();

        var comment1 = {
        "rate" : 1
        };
        var url = "http://52.90.34.144:85/api/items/" + this.props.item.id + "/rates";
      //console.log(data);
      var token = getCookie('token');
      fetch(url,
      {

      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'JWT ' + token
      },

        method: 'POST',
        body: JSON.stringify(comment1)
      })

      .then(function(res){
        if(res.ok){
          //alert("Item liked");
        } else {
          //alert("Couldn't complete :(");
        }
      })
      .catch((error) => {
        console.error(error);
      })
      }
      else if(this.state.isLiked && this.props.loginStatus == "1"){
        add = -1;
        this.setState({
          isLiked: false
        });


         var myHeaders = new Headers();

        var comment1 = {
        "rate" : 0
        };
        var url = 'http://52.90.34.144:85/api/items/' + this.props.item.id + '/rates';
      //console.log(data);
      var token = getCookie('token');
      fetch(url,
      {

      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'JWT ' + token
      },

        method: 'POST',
        body: JSON.stringify(comment1)
      })

      .then(function(res){
        if(res.ok){
          //alert("Item disliked");
        } else {
          //alert("Couldn't complete :(");
        }
      })
      .catch((error) => {
        console.error(error);
      })
      }
      else{
          alert("Please login before :(");

      }

      this.setState({
        count: clk + add
      });
  }

  componentDidMount() {
    this.refs.myComponentDiv.addEventListener('click', this.clickHandler);
  }

}

function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
}

export default Like;
