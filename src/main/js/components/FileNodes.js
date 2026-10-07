import React, { PropTypes } from 'react';
import File from './File';
import Directory from './Directory';

export default class FileNodes extends React.Component {

  static get propTypes() {
    return {
      data: PropTypes.array.isRequired,
    };
  }

  render() {
    const nodes = this.props.data.map(node => (
      node.isDirectory ?
        <Directory key={node.url} name={node.name} url={node.url.replace('/tree/', '/explore/')} />
        : <File key={node.url} name={node.name} url={node.url} />
    ));
    return (
      <ul>
        {nodes}
      </ul>
    );
  }
}